using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml;
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
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.Languages.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches;

internal static class NamesResolveUtil
{
  public static DomainResolveResult ResolveName(
    [NotNull] string name, [NotNull] IDomainResolveContext context, NameKind nameKind)
  {
    var cache = NamesCacheUtil.GetCacheFor(context.Solution, nameKind);
    var invariantNameCount = cache.GetNameCount(name);
    if (invariantNameCount != 1) return CreateInvalidResolveResult();

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

        foreach (var offset in offsets)
        {
          var primaryPsiFile = psiSourceFile.GetPrimaryPsiFile();
          if (primaryPsiFile is null) continue;

          var documentOffset = new DocumentOffset(psiSourceFile.Document, offset);
          var range = new DocumentRange(documentOffset);
          var treeTextRange = primaryPsiFile.Translate(range);
          var token = primaryPsiFile.FindTokenAt(treeTextRange.StartOffset);

          var docCommentBlock = token?.TryFindDocCommentBlock();
          if (docCommentBlock is null) continue;

          DomainResolveResult result = EmptyDomainResolveResult.Instance;
          docCommentBlock.ExecuteActionWithNames((extraction, tag) =>
          {
            if (extraction.NameKind != nameKind) return;
            
            var currentName = extraction.Name;
            var xml = docCommentBlock.GetXML(null);

            if (FindXmlElement(tag, xml) is not { } element) return;
            if (DocCommentsBuilderUtil.TryGetBuilderFor(docCommentBlock) is not { } builder) return;
            
            var segment = builder.Build(element);
            if (currentName == name && segment is { })
            {
              result = new NamedEntityDomainResolveResult(segment, docCommentBlock, tag.GetDocumentStartOffset(), nameKind);
            }
          });

          if (result is not EmptyDomainResolveResult) return result;
        }
      }
    }

    return CreateInvalidResolveResult();
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
  public static IEnumerable<ReferenceInFileDescriptor> FindAllReferencesForNamedEntity(
    NameExtraction nameExtraction, 
    [NotNull] ISolution solution)
  {
    var cache = NamesCacheUtil.GetCacheFor(solution, nameExtraction.NameKind);
    var name = nameExtraction.Name;

    if (cache.GetNameCount(name) != 1) return EmptyList<ReferenceInFileDescriptor>.Enumerable;
    
    var trigramIndex = solution.GetComponent<SourcesTrigramIndex>();
    var filesContainingQuery = trigramIndex.GetFilesContainingQuery(name, false);

    var result = new HashSet<ReferenceInFileDescriptor>();
    foreach (var file in filesContainingQuery)
    {
      if (file.GetPrimaryPsiFile() is not { } primaryFile) continue;
      var manager = LanguageManager.Instance;
      var referencesFinders = manager.TryGetCachedServices<IReferenceInCommentFinder>(primaryFile.Language).ToList();
      var indices = new LocalList<int>();
      
      FillOffsets(file.Document.GetText(), name, ref indices);
      foreach (var index in indices)
      {
        var token = primaryFile.FindTokenAt(new DocumentOffset(file.Document, index));
        if (TryFindAnyCommentNode(token) is not { } commentNode) continue;

        foreach (var finder in referencesFinders)
        {
          result.AddRange(finder.FindReferencesToNamedEntity(name, nameExtraction.NameKind, commentNode));
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