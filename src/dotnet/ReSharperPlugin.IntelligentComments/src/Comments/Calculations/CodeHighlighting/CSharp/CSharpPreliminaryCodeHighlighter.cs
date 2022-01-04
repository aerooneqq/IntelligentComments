using JetBrains.Annotations;
using JetBrains.RdBackend.Common.Features.SyntaxHighlighting.CSharp;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpPreliminaryCodeHighlighter : CodeHighlighterBase, IPreliminaryCodeHighlighter
{
  public CSharpPreliminaryCodeHighlighter([NotNull] IHighlightersProvider highlightersProvider)
    : base(highlightersProvider, new CSharpFullSyntaxHighlightingProcessor())
  {
  }
  
  
  protected override void ProcessBeforeInteriorInternal(ITreeNode element, CodeHighlightingContext context)
  {
    context.Text.Add(new HighlightedText(element.GetText()));
  }
}