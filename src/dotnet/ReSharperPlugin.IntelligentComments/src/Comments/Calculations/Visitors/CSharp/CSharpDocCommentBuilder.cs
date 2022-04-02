using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors.CSharp;

public class CSharpDocCommentBuilder : DocCommentBuilderBase
{
  public CSharpDocCommentBuilder([NotNull] IDocCommentBlock comment) : base(comment)
  {
  }
}