using JetBrains.DocumentModel;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

public record CommentBase(DocumentRange Range) : ICommentBase;

public record InlineToDoComment(
  IHighlightedText Name,
  IToDoContentSegment ToDoContentSegment, 
  DocumentRange Range
) : CommentBase(Range), IInlineToDoComment;

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

public record InlineHackComment(
  IHighlightedText Name,
  IHackContentSegment HackContentSegment, 
  DocumentRange Range
) : CommentBase(Range), IInlineHackComment;

public record InlineInvariantComment(
  IHighlightedText Name,
  IInvariantContentSegment InvariantContentSegment, 
  DocumentRange Range
) : CommentBase(Range), IInlineInvariantComment;
