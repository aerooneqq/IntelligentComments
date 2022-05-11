using IntelligentComments.Comments.Domain.Core;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.Rd.Util;
using JetBrains.ReSharper.Daemon.CodeFolding;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.TextControl.DocumentMarkup;
using Severity = JetBrains.ReSharper.Feature.Services.Daemon.Severity;

namespace IntelligentComments.Comments.Daemon;

[StaticSeverityHighlighting(Severity.INFO, typeof(IntelligentCommentsHighlightings))]
[RegisterHighlighter(
  CommentFoldingHighlightingId,
  NotRecyclable = true,
  EffectType = EffectType.FOLDING, 
  GroupId = IntelligentCommentsHighlightings.GroupId,
  TransmitUpdates = true)]
public class CommentFoldingHighlighting : CodeFoldingHighlighting, IHighlightingWithTestOutput
{
  [NotNull] public const string CommentFoldingHighlightingId = "IntelligentCommentsDocCommentFolding";
    
  public static CommentFoldingHighlighting Create([NotNull] ICommentBase comment)
  {
    return new CommentFoldingHighlighting(
      comment,
      CommentFoldingHighlightingId,
      string.Empty,
      comment.Range,
      true,
      (int) CodeFoldingPriorities.HIGHER_FOLDING_PRIORITY);
  }
  
  
  [NotNull] public ICommentBase Comment { get; }
  
  
  public CommentFoldingHighlighting(
    [NotNull] ICommentBase comment,
    [NotNull] string attributeId, 
    [NotNull] string placeholderText, 
    DocumentRange range, 
    bool collapsedByDefault, 
    int foldingPriority) 
    : base(attributeId, placeholderText, range, collapsedByDefault, foldingPriority)
  {
    Comment = comment;
  }

  
  public override string ToString()
  {
    var printer = new PrettyPrinter();
    Comment.Print(printer);
    return printer.ToString();
  }

  public string TestOutput => ToString();
}