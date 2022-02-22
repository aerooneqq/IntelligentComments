using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors.CSharp;

public class CSharpCommentProblemsCollector : CommentProblemsCollectorBase
{
  public CSharpCommentProblemsCollector([NotNull] IDocCommentBlock comment) : base(comment)
  {
  }
}