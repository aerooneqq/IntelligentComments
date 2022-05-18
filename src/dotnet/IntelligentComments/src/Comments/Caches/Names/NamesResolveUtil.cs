using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml;
using IntelligentComments.Comments.Calculations.Core;
using IntelligentComments.Comments.Calculations.Core.DocComments;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Completion;
using IntelligentComments.Comments.Domain.Core;
using IntelligentComments.Comments.Domain.Core.Content;
using IntelligentComments.Comments.Domain.Core.References;
using IntelligentComments.Comments.Domain.Impl.References;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Text;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.Util;

namespace IntelligentComments.Comments.Caches.Names;

public static class NamesResolveUtil
{
  [NotNull]
  public static IEnumerable<NameKind> AllNameKinds { get; } = new HashSet<NameKind>()
  {
    NameKind.Hack,
    NameKind.Invariant,
    NameKind.Todo
  };

  public static DomainResolveResult ResolveName(NameWithKind nameWithKind, [NotNull] IDomainResolveContext context)
  {
    var (name, nameKind) = nameWithKind;
    var cache = NamesCacheUtil.GetCacheFor(context.Solution, nameKind);
    var invariantNameCount = cache.GetNameCount(name);
    if (invariantNameCount != 1) return CreateInvalidResolveResult();

    [NotNull]
    InvalidDomainResolveResult CreateInvalidResolveResult()
    {
      return new InvalidDomainResolveResult($"Failed to resolve {nameKind} \"{name}\" for this reference");
    }
    
    var trigramIndex = context.Solution.GetComponent<SourcesTrigramIndex>();
    var filesContainingQuery = trigramIndex.GetFilesContainingQuery(name, false);
    using (ReadLockCookie.Create())
    {
      foreach (var psiSourceFile in filesContainingQuery)
      {
        var offsets = new LocalList<int>();
        FillOffsets(psiSourceFile.Document.GetText(), name, ref offsets);
        if (offsets.Count == 0) continue;
        
        var primaryPsiFile = psiSourceFile.GetPrimaryPsiFile();
        if (primaryPsiFile is null) continue;
        
        foreach (var offset in offsets)
        {
          var documentOffset = new DocumentOffset(psiSourceFile.Document, offset);
          var range = new DocumentRange(documentOffset);
          var treeTextRange = primaryPsiFile.Translate(range);
          if (primaryPsiFile.FindTokenAt(treeTextRange.StartOffset) is not { } token) continue;
          
          if (TryResolveNameInDocComment(token, nameWithKind) is { } docResult and not EmptyDomainResolveResult)
            return docResult;

          if (TryResolveNameInInlineComment(token, nameWithKind) is { } inlineResult and not EmptyDomainResolveResult)
            return inlineResult;
        }
      }
    }

    return CreateInvalidResolveResult();
  }

  [CanBeNull]
  private static DomainResolveResult TryResolveNameInInlineComment([NotNull] ITreeNode token, NameWithKind nameWithKind)
  {
    if (TryFindNearestCommentNode(token) is not { } commentNode) return null;
    if (TryFindOneNameDeclarationIn(commentNode) is not { } resolvedName) return null;

    if (resolvedName.NameWithKind != nameWithKind) return null;

    IContentSegment segment = null;
    
    //reference to invariant: CollectOperationsMustOrderOperations
    foreach (var operation in CommentOperationsUtil.CollectSpecialOperations(token))
    {
      if (operation.TryCreate(commentNode) is { Comment: IInlineComment { Content: { } content } })
      {
        segment = content;
        break;
      }
    }

    return new NamedEntityDomainResolveResult(segment, commentNode, resolvedName.EntityRange, nameWithKind.NameKind);
  }

  [CanBeNull]
  internal static ICommentNode TryFindNearestCommentNode([NotNull] ITreeNode node)
  {
    while (node is { } and not ICommentNode)
      node = node.Parent;
    
    return node as ICommentNode;
  }

  internal static CommonNamedEntityDescriptor? TryFindOneNameDeclarationIn([NotNull] ICommentNode commentNode)
  {
    var finders = LanguageManager.Instance.TryGetCachedServices<INamedEntitiesCommonFinder>(commentNode.Language);
    var names = new LocalList<CommonNamedEntityDescriptor>();
    foreach (var finder in finders)
    {
      names.AddRange(finder.FindNames(commentNode));
    }

    return names.Count != 1 ? null : names.First();
  }
  
  [CanBeNull]
  private static DomainResolveResult TryResolveNameInDocComment([NotNull] ITreeNode token, NameWithKind nameWithKind)
  {
    var (name, nameKind) = nameWithKind;
    var docCommentBlock = token?.TryFindDocCommentBlock();
    if (docCommentBlock is null) return null;

    DomainResolveResult result = null;
    ReferencesAndNamesInDocCommentFinder.ExecuteActionWithNames(docCommentBlock, (extraction, tag) =>
    {
      var currentName = extraction.Name;
      if (extraction.NameKind != nameKind || currentName != name) return;
            
      var xml = docCommentBlock.GetXML(null);
      if (FindXmlElement(tag, xml) is not { } element) return;
      if (DocCommentsBuilderUtil.TryGetBuilderFor(docCommentBlock) is not { } builder) return;
      if (DocCommentsBuilderUtil.TryGetCommonNameAttribute(tag) is not { } attribute) return;

      var segment = builder.Build(element);
      if (segment is IEntityWithInnerContentSegments entityWithInnerContentSegments)
      {
        segment = entityWithInnerContentSegments.Content;
      }
      
      result = new NamedEntityDomainResolveResult(segment, docCommentBlock, attribute.Value.GetDocumentRange(), nameKind);
    });

    return result;
  }

  [CanBeNull]
  private static XmlElement FindXmlElement(IXmlTag xmlTag, XmlNode node)
  {
    var indices = new LocalList<int>();
    ITreeNode currentNode = xmlTag;
    while (currentNode is { })
    {
      var parent = currentNode.Parent;
      if (parent is not IXmlTagContainer parentTag) break;

      indices.Add(parentTag.InnerTags.IndexOf(xmlTag));
      currentNode = parentTag;
    }

    XmlNode element = node;
    for (var i = indices.Count - 1; i >= 0; --i)
    {
      var indexOfChild = indices[i];
      var childXmlElements = node.ChildNodes.SafeOfType<XmlElement>().ToList();
      if (indexOfChild < 0 || indexOfChild >= childXmlElements.Count) return null;
      
      element = childXmlElements[indexOfChild];
    }
    
    return element as XmlElement;
  }
  
  [NotNull]
  public static IEnumerable<CommonNamedEntityDescriptor> FindAllReferencesForNamedEntity(
    NameWithKind nameExtraction, 
    [NotNull] ISolution solution)
  {
    var cache = NamesCacheUtil.GetCacheFor(solution, nameExtraction.NameKind);
    var name = nameExtraction.Name;

    if (cache.GetNameCount(name) != 1) return EmptyList<CommonNamedEntityDescriptor>.Enumerable;
    
    var trigramIndex = solution.GetComponent<SourcesTrigramIndex>();
    var filesContainingQuery = trigramIndex.GetFilesContainingQuery(name, false);

    var result = new HashSet<CommonNamedEntityDescriptor>();
    foreach (var file in filesContainingQuery)
    {
      if (file.GetPrimaryPsiFile() is not { } primaryFile) continue;
      var manager = LanguageManager.Instance;
      var referencesFinders = manager.TryGetCachedServices<INamedEntitiesCommonFinder>(primaryFile.Language).ToList();
      var indices = new LocalList<int>();
      
      FillOffsets(file.Document.GetText(), name, ref indices);
      foreach (var index in indices)
      {
        var token = primaryFile.FindTokenAt(new DocumentOffset(file.Document, index));
        if (TryFindAnyCommentNode(token) is not { } commentNode) continue;

        foreach (var finder in referencesFinders)
        {
          result.AddRange(finder.FindReferences(commentNode, nameExtraction));
        }
      }
    }

    return result;
  }

  internal static void FillOffsets(string text, string substring, ref LocalList<int> indices)
  {
    var currentIndex = 0;
      
    //can be done better but ok for now
    while (true)
    {
      if (currentIndex >= text.Length) break;
        
      var foundIndex = text.IndexOf(substring, currentIndex, StringComparison.Ordinal);
      if (foundIndex == -1) break;

      indices.Add(foundIndex);
      currentIndex = foundIndex + substring.Length;
    }
  }

  [CanBeNull]
  internal static ITreeNode TryFindAnyCommentNode([CanBeNull] ITreeNode node)
  {
    if (node is null) return null;
    if (node.TryFindDocCommentBlock() is { } docCommentBlock) return docCommentBlock;

    while (node is { } && node is not ICommentNode commentNode)
      node = node.Parent;

    return node as ICommentNode;
  }
}