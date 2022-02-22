using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.CSharp.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors.CSharp;

public class CSharpGroupOfLineCommentsBuilder : GroupOfLineCommentsBuilderBase
{
  public CSharpGroupOfLineCommentsBuilder([NotNull] ICSharpCommentNode startCommentNode) : base(startCommentNode)
  {
  }
}