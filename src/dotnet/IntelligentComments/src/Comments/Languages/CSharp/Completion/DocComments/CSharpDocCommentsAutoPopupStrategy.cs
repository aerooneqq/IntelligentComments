using IntelligentComments.Comments.Completion;
using JetBrains.Application.Parts;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.TextControl;

namespace IntelligentComments.Comments.Languages.CSharp.Completion.DocComments;

[SolutionComponent(Instantiation.DemandAnyThreadSafe)]
public class CSharpDocCommentsAutoPopupStrategy : CSharpAutoPopupStrategyBase
{
  public override bool AcceptsFile(IFile file, ITextControl textControl)
  {
    return this.MatchToken(file, textControl, node => node.TryFindDocCommentBlock() is { });
  }
}