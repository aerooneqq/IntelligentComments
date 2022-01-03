using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Daemon.CSharp.Stages;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Parsing;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpFullCodeFragmentHighlighter : CodeHighlighterBase, IFullCodeHighlighter
{
  [NotNull] private readonly CSharpIdentifierHighlighter myCSharpIdentifierHighlighter;
  

  public CSharpFullCodeFragmentHighlighter(
    [NotNull] IHighlightingAttributeIdProvider attributeIdProvider,
    [NotNull] IHighlightersProvider highlightersProvider)
    : base(highlightersProvider)
  {
    myCSharpIdentifierHighlighter = new CSharpIdentifierHighlighter(attributeIdProvider);
  }


  protected override void ProcessBeforeInteriorInternal(ITreeNode element, IHighlightedText context)
  {
    if (element.NodeType == CSharpTokenType.IDENTIFIER)
    {
      var references = element.Parent.GetReferences();
      var consumer = new MyHighlightingsConsumer();
      myCSharpIdentifierHighlighter.Highlight(element, consumer, references);
      
      if (consumer.Highlightings.Count > 0)
      {
        var highlighting = consumer.Highlightings.First().Highlighting;
        if (highlighting is ICustomAttributeIdHighlighting { AttributeId: { } attributeId })
        {
          var text = element.GetText();
          var highlighter = HighlightersProvider.TryGetReSharperHighlighter(attributeId, text.Length);
          context.Add(highlighter is { } ? new HighlightedText(text, highlighter) : new HighlightedText(text));
          return;
        }
      }
    }
    
    context.Add(new HighlightedText(element.GetText()));
  }
  
  private class MyHighlightingsConsumer : IHighlightingConsumer
  {
    [NotNull] private readonly List<HighlightingInfo> myHighlightingInfos;


    public IReadOnlyList<HighlightingInfo> Highlightings => myHighlightingInfos;

    
    public MyHighlightingsConsumer()
    {
      myHighlightingInfos = new List<HighlightingInfo>();
    }

    
    public void ConsumeHighlighting(HighlightingInfo highlightingInfo)
    {
      myHighlightingInfos.Add(highlightingInfo);
    }
  }
}