using JetBrains.DocumentModel;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

public record CommentBase(DocumentRange Range) : ICommentBase;

public record InspectionDisablingComment(
  ITextContentSegment DisabledInspections, 
  DocumentRange Range
) : CommentBase(Range), IDisablingComment;

public record InvalidComment(ITextContentSegment ErrorsSummary, DocumentRange Range) : CommentBase(Range), IInvalidComment;

public record DocCommentBase(IIntelligentCommentContent Content, DocumentRange Range) : CommentBase(Range);

public record DocComment(IIntelligentCommentContent Content, DocumentRange Range) : DocCommentBase(Content, Range), IDocComment;

public record GroupOfLineComments(ITextContentSegment Text, DocumentRange Range) : CommentBase(Range), IGroupOfLineComments;

public record MultilineComment(ITextContentSegment Text, DocumentRange Range) : CommentBase(Range), IMultilineComment;

public record InlineReferenceComment(
  IInlineReferenceContentSegment Segment,
  DocumentRange Range
) : CommentBase(Range), IInlineReferenceComment;


public record InlineCommentImpl(
  IEntityWithContentSegments Content, 
  DocumentRange Range
) : CommentBase(Range), IInlineComment;

public record InlineHackComment(
  IEntityWithContentSegments Content, 
  DocumentRange Range
) : InlineCommentImpl(Content, Range), IInlineHackComment;

public record InlineInvariantComment(
  IEntityWithContentSegments Content, 
  DocumentRange Range
) : InlineCommentImpl(Content, Range), IInlineInvariantComment;

public record InlineToDoComment(
  IEntityWithContentSegments Content, 
  DocumentRange Range
) : InlineCommentImpl(Content, Range), IInlineToDoComment;