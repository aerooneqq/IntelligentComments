using System;
using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Text;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;
using ReSharperPlugin.IntelligentComments.Comments.Calculations;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

internal static class InvariantResolveUtil
{
  public static DomainResolveResult ResolveInvariantByName(
    [NotNull] string invariantName, [NotNull] IDomainResolveContext context)
  {
    var cache = context.Solution.GetComponent<InvariantsNamesCache>();
    var invariantNameCount = cache.GetInvariantNameCount(invariantName);
    
    InvalidDomainResolveResult CreateInvalidResolveResult()
    {
      return new InvalidDomainResolveResult($"Failed to resolve invariant \"{invariantName}\" for this reference");
    }
    
    if (invariantNameCount != 1) return CreateInvalidResolveResult();

    var trigramIndex = context.Solution.GetComponent<SourcesTrigramIndex>();
    var filesContainingQuery = trigramIndex.GetFilesContainingQuery(invariantName, false);
    using (ReadLockCookie.Create())
    {
      foreach (var psiSourceFile in filesContainingQuery)
      {
        var offset = psiSourceFile.Document.GetText().IndexOf(invariantName, StringComparison.Ordinal);
        var primaryPsiFile = psiSourceFile.GetPrimaryPsiFile();
        if (primaryPsiFile is null) continue;

        var documentOffset = new DocumentOffset(psiSourceFile.Document, offset);
        var range = new DocumentRange(documentOffset);
        var treeTextRange = primaryPsiFile.Translate(range);
        var token = primaryPsiFile.FindTokenAt(treeTextRange.StartOffset);

        var docCommentBlock = token?.TryFindDocCommentBlock();
        if (docCommentBlock is null) continue;

        DomainResolveResult result = EmptyDomainResolveResult.Instance;
        docCommentBlock.ExecuteActionWithInvariants(element =>
        {
          var currentInvariantName = CommentsBuilderUtil.TryGetInvariantName(element);
          var provider = LanguageManager.Instance.GetService<IHighlightersProvider>(primaryPsiFile.Language);
          var invariant = DocCommentBuilderBase.TryBuildInvariantContentSegment(element, context.Solution, provider, false);
        
          if (currentInvariantName == invariantName && invariant is { } invariantContentSegment)
          {
            result = new InvariantDomainResolveResult(invariantContentSegment, docCommentBlock, documentOffset);
          }
        });

        if (result is not EmptyDomainResolveResult) return result;
      }
    }

    return CreateInvalidResolveResult();
  }
  
  [NotNull]
  public static IEnumerable<ReferenceInFileDescriptor> FindAllReferencesForInvariantName(
    [NotNull] string name, 
    [NotNull] ISolution solution)
  {
    var cache = solution.GetComponent<InvariantsNamesCache>();
    if (cache.GetInvariantNameCount(name) != 1) return EmptyList<ReferenceInFileDescriptor>.Enumerable;
    
    var trigramIndex = solution.GetComponent<SourcesTrigramIndex>();
    var filesContainingQuery = trigramIndex.GetFilesContainingQuery(name, false);

    var result = new LocalList<ReferenceInFileDescriptor>();
    foreach (var file in filesContainingQuery)
    {
      var text = file.Document.GetText();
      var indices = new LocalList<int>();
      var currentIndex = 0;
      
      //can be done better but ok for now
      while (true)
      {
        if (currentIndex >= text.Length) break;
        
        var foundIndex = text.IndexOf(name, currentIndex, StringComparison.Ordinal);
        if (foundIndex == -1) break;

        indices.Add(foundIndex);
        currentIndex = foundIndex + name.Length;
      }
      
      if (file.GetPrimaryPsiFile() is not { } primaryFile) continue;

      var manager = LanguageManager.Instance;
      var referencesFinders = manager.TryGetCachedServices<IReferenceInCommentFinder>(primaryFile.Language).ToList();
      
      foreach (var index in indices)
      {
        var token = primaryFile.FindTokenAt(new DocumentOffset(file.Document, index));
        if (TryFindAnyCommentNode(token) is not { } commentNode) continue;

        foreach (var finder in referencesFinders)
        {
          result.AddRange(finder.FindReferencesToInvariant(name, commentNode));
        }
      }
    }

    return result.ResultingList();
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