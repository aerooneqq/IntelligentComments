using JetBrains.DocumentModel;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

public record CommentBase(DocumentRange Range) : ICommentBase
{
  public int CreateIdentifier()
  {
    return Range.TextRange.GetHashCode();
  }
}

public record DocCommentBase(IIntelligentCommentContent Content, DocumentRange Range) : CommentBase(Range);

public record DocComment(IIntelligentCommentContent Content, DocumentRange Range) : DocCommentBase(Content, Range), IDocComment;

public record IntelligentComment(
  DocumentRange Range,
  IIntelligentCommentContent Content
) : DocCommentBase(Content, Range), IIntelligentComment;

public record GroupOfLineComments(ITextContentSegment Text, DocumentRange Range) : CommentBase(Range), IGroupOfLineComments;

public record MultilineComment(ITextContentSegment Text, DocumentRange Range) : CommentBase(Range), IMultilineComment;