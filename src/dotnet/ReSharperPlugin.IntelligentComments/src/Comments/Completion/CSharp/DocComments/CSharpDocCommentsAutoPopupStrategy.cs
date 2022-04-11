using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.TextControl;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;

[SolutionComponent]
public class CSharpDocCommentsAutoPopupStrategy : CSharpAutoPopupStrategyBase
{
  public override bool AcceptsFile(IFile file, ITextControl textControl)
  {
    return this.MatchToken(file, textControl, node => node.TryFindDocCommentBlock() is { });
  }
}