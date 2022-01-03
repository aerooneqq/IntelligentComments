using JetBrains.Annotations;
using JetBrains.RdBackend.Common.Features.SyntaxHighlighting.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Impl.Tree;
using JetBrains.ReSharper.Psi.CSharp.Parsing;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public abstract class CodeHighlighterBase : ICodeHighlighter
{
  [NotNull] protected readonly IHighlightersProvider HighlightersProvider;
  [NotNull] protected readonly CSharpFullSyntaxHighlightingProcessor CSharpFullSyntaxHighlightingProcessor;
  

  protected CodeHighlighterBase([NotNull] IHighlightersProvider highlightersProvider)
  {
    HighlightersProvider = highlightersProvider;
    CSharpFullSyntaxHighlightingProcessor = new CSharpFullSyntaxHighlightingProcessor();
  }
  
  
  public bool InteriorShouldBeProcessed(ITreeNode element, IHighlightedText context) => true;
  
  public bool IsProcessingFinished(IHighlightedText context) => false;

  public virtual void ProcessBeforeInterior(ITreeNode element, IHighlightedText context)
  {
    if (!AcceptNode(element)) return;

    var nodeType = element.NodeType;
    if (nodeType == CSharpTokenType.WHITE_SPACE)
    {
      context.Add(new HighlightedText(element.GetText()));
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
        context.Add(highlighter is { } ? new HighlightedText(text, highlighter) : new HighlightedText(text));
        return;
      }
    }

    ProcessBeforeInteriorInternal(element, context);
  }

  protected abstract void ProcessBeforeInteriorInternal([NotNull] ITreeNode element, [NotNull] IHighlightedText context);
  protected virtual bool AcceptNode([NotNull] ITreeNode node)
  {
    return node.FirstChild is null;
  }

  public void ProcessAfterInterior(ITreeNode element, IHighlightedText context)
  {
  }
}