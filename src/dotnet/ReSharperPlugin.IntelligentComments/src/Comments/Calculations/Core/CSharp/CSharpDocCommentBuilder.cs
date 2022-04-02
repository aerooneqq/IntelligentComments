using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.CSharp;

public class CSharpDocCommentBuilder : DocCommentBuilderBase
{
  public CSharpDocCommentBuilder([NotNull] IDocCommentBlock comment) : base(comment)
  {
  }
}