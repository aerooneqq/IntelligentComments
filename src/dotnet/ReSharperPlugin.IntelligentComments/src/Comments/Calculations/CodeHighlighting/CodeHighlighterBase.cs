using JetBrains.Annotations;
using JetBrains.RdBackend.Common.Features.SyntaxHighlighting.CSharp;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Impl.Tree;
using JetBrains.ReSharper.Psi.CSharp.Parsing;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public abstract class CodeHighlighterBase : IRecursiveElementProcessor
{
  [NotNull] protected readonly IHighlightersProvider HighlightersProvider;
  [NotNull] protected readonly CSharpFullSyntaxHighlightingProcessor CSharpFullSyntaxHighlightingProcessor;
  [NotNull] protected readonly IHighlightedText HighlightedText;
  [NotNull] protected readonly ITreeNode Owner;
  
  public bool ProcessingIsFinished => false;
  [NotNull] public IHighlightedText Text => HighlightedText;
  
  
  protected CodeHighlighterBase(    
    [NotNull] IHighlightersProvider highlightersProvider,
    [NotNull] ITreeNode owner)
  {
    HighlightersProvider = highlightersProvider;
    Owner = owner;
    CSharpFullSyntaxHighlightingProcessor = new CSharpFullSyntaxHighlightingProcessor();
    HighlightedText = new HighlightedText(string.Empty );
  }
  
  
  public bool InteriorShouldBeProcessed(ITreeNode element) => true;

  public virtual void ProcessBeforeInterior(ITreeNode element)
  {
    if (!AcceptNode(element)) return;

    var nodeType = element.NodeType;
    if (nodeType == CSharpTokenType.WHITE_SPACE)
    {
      HighlightedText.Add(new HighlightedText(element.GetText()));
      return;
    }
    
    if (element is CSharpTokenBase token)
    {
      var type = token.GetTokenType();
      var attributeId = CSharpFullSyntaxHighlightingProcessor.GetAttributeId(type);
      if (attributeId is { })
      {
        var text = element.GetText();
        var highlighter = HighlightersProvider.TryGetReSharperHighlighter(attributeId, text.Length);
        HighlightedText.Add(highlighter is { } ? new HighlightedText(text, highlighter) : new HighlightedText(text));
        return;
      }
    }

    ProcessBeforeInteriorInternal(element);
  }

  protected abstract void ProcessBeforeInteriorInternal(ITreeNode element);
  protected virtual bool AcceptNode(ITreeNode node)
  {
    return node.FirstChild is null;
  }

  public void ProcessAfterInterior(ITreeNode element)
  {
  }
}