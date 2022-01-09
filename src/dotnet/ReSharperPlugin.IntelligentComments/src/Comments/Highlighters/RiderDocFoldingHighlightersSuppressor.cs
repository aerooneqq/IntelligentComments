using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features.Daemon;
using JetBrains.ReSharper.Daemon.CodeFolding;
using JetBrains.TextControl.DocumentMarkup;
using JetBrains.Util.Logging;

namespace ReSharperPlugin.IntelligentComments.Comments.Highlighters;

[SolutionComponent]
public class RiderDocFoldingHighlightersSuppressor : IRiderHighlighterSuppressor
{
  public bool IsSuppressed(IHighlighter highlighter)
  {
    return highlighter.UserData is CodeFoldingHighlighting
    {
      AttributeId: CodeFoldingAttributes.DOCUMENTATION_COMMENTS_FOLDING_ATTRIBUTE or CodeFoldingAttributes.COMMENTS_FOLDING_ATTRIBUTE
    };
  }
}