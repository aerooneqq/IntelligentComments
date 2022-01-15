using JetBrains.Annotations;
using JetBrains.ReSharper.Daemon.SyntaxHighlighting;
using JetBrains.ReSharper.Psi.CSharp.Parsing;
using JetBrains.ReSharper.Psi.ExtensionsAPI.Tree;
using JetBrains.ReSharper.Psi.Parsing;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public abstract class CodeHighlighterBase
{
  [NotNull] protected readonly IHighlightersProvider HighlightersProvider;
  [NotNull] protected readonly SyntaxHighlightingProcessor SyntaxHighlightingProcessor;
  

  protected CodeHighlighterBase(
    [NotNull] IHighlightersProvider highlightersProvider, 
    [NotNull] SyntaxHighlightingProcessor syntaxHighlightingProcessor)
  {
    HighlightersProvider = highlightersProvider;
    SyntaxHighlightingProcessor = syntaxHighlightingProcessor;
  }
  
  
  public bool InteriorShouldBeProcessed(ITreeNode element, CodeHighlightingContext context) => true;
  
  public bool IsProcessingFinished(CodeHighlightingContext context) => false;

  public virtual void ProcessBeforeInterior(ITreeNode element, CodeHighlightingContext context)
  {
    if (!AcceptNode(element)) return;

    NodeType nodeType = element.NodeType;
    if (nodeType == CSharpTokenType.WHITE_SPACE)
    {
      context.Text.Add(new HighlightedText(element.GetText()));
      return;
    }
    
    if (!TryProcessSyntax(element, context))
    {
      ProcessBeforeInteriorInternal(element, context); 
    }
  }
  
  protected virtual bool TryProcessSyntax(ITreeNode element, CodeHighlightingContext context)
  {
    if (element is ITokenNode token)
    {
      TokenNodeType type = token.GetTokenType();
      string attributeId = SyntaxHighlightingProcessor.GetAttributeId(type);
      if (attributeId is { })
      {
        string text = element.GetText();
        TextHighlighter highlighter = HighlightersProvider.TryGetReSharperHighlighter(attributeId, text.Length);
        context.Text.Add(highlighter is { } ? new HighlightedText(text, highlighter) : new HighlightedText(text));
        return true;
      }
    }

    return false;
  }

  protected abstract void ProcessBeforeInteriorInternal([NotNull] ITreeNode element, [NotNull] CodeHighlightingContext context);
  protected virtual bool AcceptNode([NotNull] ITreeNode node)
  {
    return node.FirstChild is null;
  }

  public void ProcessAfterInterior(ITreeNode element, CodeHighlightingContext context)
  {
  }
}