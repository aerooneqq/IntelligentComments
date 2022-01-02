using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Parsing;
using JetBrains.ReSharper.Psi.Resolve;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public class RecursiveElementsHighlighter : IRecursiveElementProcessor
{
  [NotNull] private readonly IHighlightersProvider myHighlightersProvider;
  [NotNull] private readonly IList<(string, DocumentRange)> mySyntaxHighlightings;
  [NotNull] private readonly IHighlightedText myHighlightedText;
  [NotNull] private readonly ISymbolTable mySymbolTable;

  private int myCurrentSyntaxHighlightingIndex;
  

  [NotNull] public IHighlightedText Text => myHighlightedText;
  public bool ProcessingIsFinished => false;

  
  public RecursiveElementsHighlighter(
    [NotNull] IHighlightersProvider highlightersProvider,
    [NotNull] ITreeNode owner,
    [NotNull] IList<(string, DocumentRange)> syntaxHighlightings)
  {
    myHighlightersProvider = highlightersProvider;
    mySyntaxHighlightings = syntaxHighlightings;
    myHighlightedText = new HighlightedText(string.Empty);
    mySymbolTable = SymbolTableBuilder.GetTable(owner);
  }
  

  public bool InteriorShouldBeProcessed(ITreeNode element) => true;
  
  public void ProcessBeforeInterior(ITreeNode element)
  {
    if (element.FirstChild is { }) return;

    var nodeType = element.NodeType;
    if (nodeType == CSharpTokenType.WHITE_SPACE)
    {
      myHighlightedText.Add(new HighlightedText(element.GetText()));
      return;
    }

    if (myCurrentSyntaxHighlightingIndex < mySyntaxHighlightings.Count)
    {
      var range = element.GetDocumentRange();
      var (attributeId, documentRange) = mySyntaxHighlightings[myCurrentSyntaxHighlightingIndex];
      if (range == documentRange)
      {
        var text = element.GetText();
        var highlighter = myHighlightersProvider.TryGetReSharperHighlighter(attributeId, text.Length);
        myHighlightedText.Add(new HighlightedText(text, highlighter));
        ++myCurrentSyntaxHighlightingIndex;
        return;
      } 
    }

    if (element.NodeType == CSharpTokenType.IDENTIFIER)
    {
      var name = element.GetText();
      var symbolInfos = mySymbolTable.GetSymbolInfos(name);
      if (symbolInfos.Count > 0)
      {
        var symbol = symbolInfos.First();
        var declaredElement = symbol.GetDeclaredElement();
        var highlighter = myHighlightersProvider.TryGetReSharperHighlighter(name.Length, declaredElement);
        myHighlightedText.Add(new HighlightedText(name, highlighter));
        return;
      }
    }
    
    myHighlightedText.Add(new HighlightedText(element.GetText()));
  }

  public void ProcessAfterInterior(ITreeNode element)
  {
  }
}