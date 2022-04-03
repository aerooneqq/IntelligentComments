using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.Languages.CSharp;

public class CSharpDocCommentBuilder : DocCommentBuilderBase
{
  public CSharpDocCommentBuilder([NotNull] IDocCommentBlock comment) : base(comment)
  {
  }
}