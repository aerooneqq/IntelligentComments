using JetBrains.DocumentModel;
using JetBrains.ReSharper.Daemon.CodeFolding;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Daemon;

public class DocCommentFoldingHighlighting : CodeFoldingHighlighting
{
  public static DocCommentFoldingHighlighting Create(IDocComment comment)
  {
    return new DocCommentFoldingHighlighting(
      comment,
      CodeFoldingAttributes.DOCUMENTATION_COMMENTS_FOLDING_ATTRIBUTE,
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