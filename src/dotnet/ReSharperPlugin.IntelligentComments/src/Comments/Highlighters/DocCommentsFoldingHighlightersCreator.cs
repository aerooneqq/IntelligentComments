using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features.Daemon;
using JetBrains.RdBackend.Common.Features.Daemon.HighlighterTypes.Foldings;
using JetBrains.ReSharper.Daemon.CodeFolding;
using JetBrains.Rider.Model;
using JetBrains.TextControl.DocumentMarkup;

namespace ReSharperPlugin.IntelligentComments.Comments.Highlighters
{
  [SolutionComponent]
  public class DocCommentsFoldingHighlightersCreator : IRiderHighlighterModelCreator
  {
    [NotNull] private readonly RiderFoldingHighlighterModelCreator myDefaultCreator;

    
    public int Priority => 100500;

    
    public DocCommentsFoldingHighlightersCreator([NotNull] RiderFoldingHighlighterModelCreator defaultCreator)
    {
      myDefaultCreator = defaultCreator;
    }


    public bool Accept(IHighlighter highlighter)
    {
      return highlighter.UserData is CodeFoldingHighlighting
      {
        AttributeId: CodeFoldingAttributes.DOCUMENTATION_COMMENTS_FOLDING_ATTRIBUTE
      };
    }

    public HighlighterModel CreateModel(long id, DocumentVersion documentVersion, IHighlighter highlighter, int shift)
    {
      var model = myDefaultCreator.CreateModel(id, documentVersion, highlighter, shift) as FoldingHighlighterModel;
      Assertion.Assert(model is { }, "model is { }");

      var commentIdentifier = highlighter.Range.GetHashCode();
      return new RdDocCommentFoldingModel(
        commentIdentifier,
        model.Layer,
        model.IsExactRange,
        model.DocumentVersion,
        false,
        false,
        model.IsThinErrorStripeMark,
        model.TextToHighlight,
        model.TextAttributesKey,
        model.Id,
        model.Properties,
        model.Start,
        model.End);
    }
  }
}