using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features.Daemon;
using JetBrains.RdBackend.Common.Features.Daemon.HighlighterTypes.DefaultHighlighters;
using JetBrains.Rider.Model;
using JetBrains.TextControl.DocumentMarkup;
using ReSharperPlugin.IntelligentComments.Comments.Daemon;
using ReSharperPlugin.IntelligentComments.Comments.Domain;
using ReSharperPlugin.IntelligentComments.Protocol;

namespace ReSharperPlugin.IntelligentComments.Comments.Highlighters;

[SolutionComponent]
public class DocCommentsFoldingHighlightersCreator : IRiderHighlighterModelCreator
{
  [NotNull] private readonly RiderDefaultHighlighterModelCreator myDefaultCreator;

    
  public int Priority => HighlighterModelCreatorPriorities.CODE_FOLDING + 1;

    
  public DocCommentsFoldingHighlightersCreator(
    [NotNull] RiderDefaultHighlighterModelCreator defaultCreator, 
    [NotNull] ProtocolModelRegistrar protocolModelRegistrar)
  {
    myDefaultCreator = defaultCreator;
  }


  public bool Accept(IHighlighter highlighter)
  {
    return highlighter.UserData is DocCommentFoldingHighlighting;
  }

  public HighlighterModel CreateModel(long id, DocumentVersion documentVersion, IHighlighter highlighter, int shift)
  {
    var docCommentFoldingHighlighting = highlighter.UserData as DocCommentFoldingHighlighting;
    Assertion.Assert(docCommentFoldingHighlighting is { }, "docCommentFoldingHighlighting is { }");
      
    var model = myDefaultCreator.CreateModel(id, documentVersion, highlighter, shift);
    Assertion.Assert(model is { }, "model is { }");

    var commentIdentifier = highlighter.Range.GetHashCode();
    var rdDocComment = docCommentFoldingHighlighting.Comment.ToRdComment() as RdDocComment;
    Assertion.Assert(rdDocComment is { }, "rdDocComment is { }");
      
    return new RdDocCommentFoldingModel(
      commentIdentifier,
      rdDocComment,
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