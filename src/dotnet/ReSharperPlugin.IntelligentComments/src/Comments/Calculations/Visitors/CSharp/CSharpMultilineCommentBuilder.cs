using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors.CSharp;

public class CSharpMultilineCommentBuilder : MultilineCommentBuilderBase
{
  public CSharpMultilineCommentBuilder([NotNull] ICSharpCommentNode commentNode) : base(commentNode)
  {
  }
}