using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.Application.Settings;
using JetBrains.ReSharper.Daemon.CSharp.Stages;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public class RecursiveElementsHighlighter : IRecursiveElementProcessor
{
  private class HighlightingsConsumer : IHighlightingConsumer
  {
    [NotNull] private readonly List<HighlightingInfo> myHighlightingInfos;
    
    
    public IReadOnlyList<HighlightingInfo> Highlightings => myHighlightingInfos;


    public HighlightingsConsumer()
    {
      myHighlightingInfos = new List<HighlightingInfo>();
    }
    
    
    public void ConsumeHighlighting(HighlightingInfo highlightingInfo)
    {
      myHighlightingInfos.Add(highlightingInfo);
    }
  }
  
  [NotNull] private readonly CSharpIdentifierHighlighter myCSharpIdentifierHighlighter;
  [NotNull] private readonly IHighlightingSettingsManager myHighlightingSettingsManager;
  [NotNull] private readonly IHighlightedText myHighlightedText;
  [NotNull] private readonly IContextBoundSettingsStore myContextBoundSettingsStore;

  [CanBeNull] private readonly IPsiSourceFile myPsiSourceFile;

  [NotNull] public IHighlightedText Text => myHighlightedText;
  public bool ProcessingIsFinished => false;

  
  public RecursiveElementsHighlighter(
    [NotNull] CSharpIdentifierHighlighter cSharpIdentifierHighlighter,
    [NotNull] IHighlightingSettingsManager highlightingSettingsManager,
    [NotNull] ITreeNode owner)
  {
    myHighlightedText = new HighlightedText(string.Empty);
    myCSharpIdentifierHighlighter = cSharpIdentifierHighlighter;
    myHighlightingSettingsManager = highlightingSettingsManager;
    myPsiSourceFile = owner.GetSourceFile();
    myContextBoundSettingsStore = owner.GetSettingsStore();
  }
  

  public bool InteriorShouldBeProcessed(ITreeNode element) => true;
  
  public void ProcessBeforeInterior(ITreeNode element)
  {
    if (element.FirstChild is { }) return;
    
    var references = element.GetReferences();
    var highlightingConsumer = new HighlightingsConsumer();
    myCSharpIdentifierHighlighter.Highlight(element, highlightingConsumer, references);

    if (highlightingConsumer.Highlightings.Count == 0)
    {
      myHighlightedText.Add(new HighlightedText(element.GetText()));
      return;
    }
    
    var attributeId = highlightingConsumer.Highlightings.First().GetAttributeId(
      myHighlightingSettingsManager, myPsiSourceFile, myPsiSourceFile?.GetSolution(), myContextBoundSettingsStore);

    if (attributeId is null)
    {
      myHighlightedText.Add(new HighlightedText(element.GetText()));
      return;
    }

    var text = element.GetText();
    var textHighlighter = new TextHighlighter(
      attributeId, 0, text.Length, TextHighlighterAttributes.DefaultAttributes, IsResharperHighlighter: true);
    
    myHighlightedText.Add(new HighlightedText(text, new [] { textHighlighter }));
  }

  public void ProcessAfterInterior(ITreeNode element)
  {
  }
}