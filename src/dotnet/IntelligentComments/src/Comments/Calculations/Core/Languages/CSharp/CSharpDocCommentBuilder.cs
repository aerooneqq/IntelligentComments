using IntelligentComments.Comments.Calculations.Core.DocComments;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;

namespace IntelligentComments.Comments.Calculations.Core.Languages.CSharp;

public class CSharpDocCommentBuilder : DocCommentBuilderBase
{
  public CSharpDocCommentBuilder([NotNull] IDocCommentBlock comment) : base(comment)
  {
  }
}