using JetBrains.Application.Parts;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features.Daemon;
using JetBrains.ReSharper.Daemon.CodeFolding;
using JetBrains.TextControl.DocumentMarkup;

namespace IntelligentComments.Rider.Comments.Highlighters;

[SolutionComponent(Instantiation.DemandAnyThreadSafe)]
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