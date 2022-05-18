using IntelligentComments.Comments.Calculations.Core.DocComments;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;

namespace IntelligentComments.Comments.Languages.CSharp.Calculations;

public class CSharpDocCommentBuilder : DocCommentBuilderBase
{
  public CSharpDocCommentBuilder([NotNull] IDocCommentBlock comment) : base(comment)
  {
  }
}