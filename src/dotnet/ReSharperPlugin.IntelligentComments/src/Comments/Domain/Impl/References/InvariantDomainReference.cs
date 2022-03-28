using System;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Text;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.ReSharper.Resources.Shell;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;
using ReSharperPlugin.IntelligentComments.Comments.Calculations;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;
using ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class InvariantDomainReference : DomainReferenceBase, IInvariantDomainReference
{
  public string InvariantName { get; }
  
  
  public InvariantDomainReference(string name) : base(name)
  {
    InvariantName = name;
  }


  public override ResolveResult Resolve(IResolveContext context) => InvariantResolveUtil.ResolveInvariantByName(InvariantName, context);
}

internal static class InvariantResolveUtil
{
  public static ResolveResult ResolveInvariantByName([NotNull] string invariantName, IResolveContext context)
  {
    var cache = context.Solution.GetComponent<InvariantsNamesCache>();
    var invariantNameCount = cache.GetInvariantNameCount(invariantName);
    
    InvalidResolveResult CreateInvalidResolveResult()
    {
      return new InvalidResolveResult($"Failed to resolve invariant \"{invariantName}\" for this reference");
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

        ResolveResult result = EmptyResolveResult.Instance;
        docCommentBlock.ExecuteActionWithInvariants(element =>
        {
          var currentInvariantName = CommentsBuilderUtil.TryGetInvariantName(element);
          var provider = LanguageManager.Instance.GetService<IHighlightersProvider>(primaryPsiFile.Language);
          var invariant = DocCommentBuilderBase.TryBuildInvariantContentSegment(element, context.Solution, provider, false);
        
          if (currentInvariantName == invariantName && invariant is { } invariantContentSegment)
          {
            result = new InvariantResolveResult(invariantContentSegment, docCommentBlock, documentOffset);
          }
        });

        if (result is not EmptyResolveResult) return result;
      }
    }

    return CreateInvalidResolveResult();
  }
}

public class InvariantResolveResult : ResolveResult
{
  [NotNull] public IInvariantContentSegment Invariant { get; }
  [NotNull] public IDocCommentBlock ParentDocCommentBlock { get; }
  public DocumentOffset InvariantDocumentOffset { get; }


  public InvariantResolveResult(
    [NotNull] IInvariantContentSegment invariant, 
    [NotNull] IDocCommentBlock parentBlock, 
    DocumentOffset invariantDocumentOffset)
  {
    Invariant = invariant;
    ParentDocCommentBlock = parentBlock;
    InvariantDocumentOffset = invariantDocumentOffset;
  }
}