using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpCommentProblemsCollector : CommentProblemsCollectorBase
{
  public CSharpCommentProblemsCollector([NotNull] IDocCommentBlock comment) : base(comment)
  {
  }
}