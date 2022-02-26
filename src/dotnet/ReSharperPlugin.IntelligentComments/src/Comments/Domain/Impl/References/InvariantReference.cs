using System;
using System.Linq;
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
    if (invariantNameCount != 1) return EmptyResolveResult.Instance;

    var trigramIndex = context.Solution.GetComponent<SourcesTrigramIndex>();
    var filesContainingQuery = trigramIndex.GetFilesContainingQuery(InvariantName, false);
    if (filesContainingQuery.FirstOrDefault() is not { } psiSourceFile) return EmptyResolveResult.Instance;

    using (ReadLockCookie.Create())
    {
      var index = psiSourceFile.Document.GetText().IndexOf(InvariantName, StringComparison.Ordinal);
      var primaryPsiFile = psiSourceFile.GetPrimaryPsiFile();
      if (primaryPsiFile is null) return EmptyResolveResult.Instance;
      
      var range = new DocumentRange(psiSourceFile.Document, index);
      var treeTextRange = primaryPsiFile.Translate(range);
      var token = primaryPsiFile.FindTokenAt(treeTextRange.StartOffset);

      var docCommentBlock = token?.TryFindDocCommentBlock();
      if (docCommentBlock is null) return EmptyResolveResult.Instance;

      ResolveResult result = EmptyResolveResult.Instance;
      docCommentBlock.ExecuteActionsWithInvariants(element =>
      {
        var invariantName = CommentsBuilderUtil.TryGetInvariantName(element);
        var provider = LanguageManager.Instance.GetService<IHighlightersProvider>(primaryPsiFile.Language);
      
        if (invariantName == InvariantName &&
            DocCommentBuilderBase.TryBuildInvariantContentSegment(element, provider) is { } invariantContentSegment)
        {
          result = new InvariantResolveResult(invariantContentSegment);
        }
      });

      return result; 
    }
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