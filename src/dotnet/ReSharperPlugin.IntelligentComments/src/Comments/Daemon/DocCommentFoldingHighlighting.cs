using JetBrains.DocumentModel;
using JetBrains.ReSharper.Daemon.CodeFolding;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.TextControl.DocumentMarkup;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Daemon;

[StaticSeverityHighlighting(Severity.INFO, typeof(IntelligentCommentsHighlightings))]
[RegisterHighlighter(DocCommentAttributeId, EffectType = EffectType.FOLDING, GroupId = IntelligentCommentsHighlightings.GroupId, TransmitUpdates = true)]
public class DocCommentFoldingHighlighting : CodeFoldingHighlighting
{
  public const string DocCommentAttributeId = "IntelligentCommentsDocCommentFolding";
    
  public static DocCommentFoldingHighlighting Create(IDocComment comment)
  {
    return new DocCommentFoldingHighlighting(
      comment,
      DocCommentAttributeId,
      string.Empty,
      comment.CommentOwnerPointer.GetTreeNode().GetDocumentRange(),
      true,
      (int) CodeFoldingPriorities.HIGHER_FOLDING_PRIORITY);
  }
  
  
  public IDocComment Comment { get; }
  
  
  public DocCommentFoldingHighlighting(
    IDocComment comment,
    string attributeId, 
    string placeholderText, 
    DocumentRange range, 
    bool collapsedByDefault, 
    int foldingPriority) 
    : base(attributeId, placeholderText, range, collapsedByDefault, foldingPriority)
  {
    Comment = comment;
  }
}