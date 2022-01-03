using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.CSharp.Parsing;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public class PreliminaryCodeHighlighter : CodeHighlighterBase
{
  public PreliminaryCodeHighlighter(
    [NotNull] IHighlightersProvider highlightersProvider,
    [NotNull] ITreeNode owner)
    : base(highlightersProvider, owner)
  {
  }
  
  
  protected override void ProcessBeforeInteriorInternal(ITreeNode element)
  {
    HighlightedText.Add(new HighlightedText(element.GetText()));
  }
}