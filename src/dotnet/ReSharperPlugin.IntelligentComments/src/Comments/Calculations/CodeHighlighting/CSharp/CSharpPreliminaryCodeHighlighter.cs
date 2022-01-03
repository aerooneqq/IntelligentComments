using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpPreliminaryCodeHighlighter : CodeHighlighterBase, IPreliminaryCodeHighlighter
{
  public CSharpPreliminaryCodeHighlighter(
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