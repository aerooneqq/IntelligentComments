using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

public record DocCommentBase(
  IIntelligentCommentContent Content,
  ITreeNodePointer<ITreeNode> CommentOwnerPointer) : ICommentBase
{
  public int CreateIdentifier()
  {
    return CommentOwnerPointer.GetTreeNode().GetDocumentRange().TextRange.GetHashCode();
  }
}

public record DocComment(
  IIntelligentCommentContent Content,
  ITreeNodePointer<ITreeNode> CommentOwnerPointer) : DocCommentBase(Content, CommentOwnerPointer), IDocComment;

public record IntelligentComment(
  ITreeNodePointer<ITreeNode> CommentOwnerPointer,
  IIntelligentCommentContent Content) : DocCommentBase(Content, CommentOwnerPointer), IIntelligentComment;