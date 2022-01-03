using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Daemon.CSharp.Stages;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi.CSharp.Parsing;
using JetBrains.ReSharper.Psi.ExtensionsAPI.Tree;
using JetBrains.ReSharper.Psi.Resolve;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public class CodeFragmentHighlighter : CodeHighlighterBase
{
  [NotNull] private readonly CSharpIdentifierHighlighter myCSharpIdentifierHighlighter;
  [CanBeNull] private readonly IReferenceProvider myReferenceProvider;


  public CodeFragmentHighlighter(
    [NotNull] IHighlightingAttributeIdProvider attributeIdProvider,
    [NotNull] IHighlightersProvider highlightersProvider,
    [NotNull] ITreeNode owner)
    : base(highlightersProvider, owner)
  {
    myCSharpIdentifierHighlighter = new CSharpIdentifierHighlighter(attributeIdProvider);
    myReferenceProvider = (owner.GetContainingFile() as IFileImpl)?.ReferenceProvider;
  }


  protected override void ProcessBeforeInteriorInternal(ITreeNode element)
  {
    if (element.NodeType == CSharpTokenType.IDENTIFIER)
    {
      var references = element.Parent.GetReferences(myReferenceProvider);
      var consumer = new MyHighlightingsConsumer();
      myCSharpIdentifierHighlighter.Highlight(element, consumer, references);
      
      if (consumer.Highlightings.Count > 0)
      {
        var highlighting = consumer.Highlightings.First().Highlighting;
        if (highlighting is ICustomAttributeIdHighlighting { AttributeId: { } attributeId })
        {
          var text = element.GetText();
          var highlighter = HighlightersProvider.TryGetReSharperHighlighter(attributeId, text.Length);
          HighlightedText.Add(highlighter is { } ? new HighlightedText(text, highlighter) : new HighlightedText(text));
          return;
        }
      }
    }
    
    HighlightedText.Add(new HighlightedText(element.GetText()));
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