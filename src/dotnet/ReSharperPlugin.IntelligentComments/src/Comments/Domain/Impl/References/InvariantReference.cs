using System;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Text;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Resources.Shell;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;
using ReSharperPlugin.IntelligentComments.Comments.Calculations;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;
using ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class InvariantReference : ReferenceBase, IInvariantReference
{
  public string InvariantName { get; }
  
  
  public InvariantReference(string name) : base(name)
  {
    InvariantName = name;
  }


  public override ResolveResult Resolve(IResolveContext context)
  {
    var cache = context.Solution.GetComponent<InvariantsNamesCache>();
    var invariantNameCount = cache.GetInvariantNameCount(InvariantName);
    
    InvalidResolveResult CreateInvalidResolveResult()
    {
      return new InvalidResolveResult($"Failed to resolve invariant \"{InvariantName}\" for this the reference");
    }
    
    if (invariantNameCount != 1) return CreateInvalidResolveResult();

    var trigramIndex = context.Solution.GetComponent<SourcesTrigramIndex>();
    var filesContainingQuery = trigramIndex.GetFilesContainingQuery(InvariantName, false);
    using (ReadLockCookie.Create())
    {
      foreach (var psiSourceFile in filesContainingQuery)
      {
        var index = psiSourceFile.Document.GetText().IndexOf(InvariantName, StringComparison.Ordinal);
        var primaryPsiFile = psiSourceFile.GetPrimaryPsiFile();
        if (primaryPsiFile is null) continue;
      
        var range = new DocumentRange(psiSourceFile.Document, index);
        var treeTextRange = primaryPsiFile.Translate(range);
        var token = primaryPsiFile.FindTokenAt(treeTextRange.StartOffset);

        var docCommentBlock = token?.TryFindDocCommentBlock();
        if (docCommentBlock is null) continue;

        ResolveResult result = EmptyResolveResult.Instance;
        docCommentBlock.ExecuteActionWithInvariants(element =>
        {
          var invariantName = CommentsBuilderUtil.TryGetInvariantName(element);
          var provider = LanguageManager.Instance.GetService<IHighlightersProvider>(primaryPsiFile.Language);
          var invariant = DocCommentBuilderBase.TryBuildInvariantContentSegment(element, context.Solution, provider, false);
        
          if (invariantName == InvariantName && invariant is { } invariantContentSegment)
          {
            result = new InvariantResolveResult(invariantContentSegment);
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
  

  public InvariantResolveResult([NotNull] IInvariantContentSegment invariant)
  {
    Invariant = invariant;
  }
}