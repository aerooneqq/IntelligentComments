using IntelligentComments.Comments.Domain.Core;
using IntelligentComments.Comments.Domain.Core.Content;
using JetBrains.DocumentModel;
using JetBrains.Rd.Util;

namespace IntelligentComments.Comments.Domain.Impl;

public record CommentBase(DocumentRange Range) : ICommentBase
{
  public virtual void Print(PrettyPrinter printer)
  {
    printer.Println(Range.ToString());
  }
}

public record InspectionDisablingComment(
  ITextContentSegment DisabledInspections,
  DocumentRange Range
) : CommentBase(Range), IDisablingComment
{
  public override void Print(PrettyPrinter printer)
  {
    base.Print(printer);
    DisabledInspections.Print(printer);
  }
}

public record InvalidComment(
  ITextContentSegment ErrorsSummary, DocumentRange Range) : CommentBase(Range), IInvalidComment
{
  public override void Print(PrettyPrinter printer)
  {
    base.Print(printer);
    ErrorsSummary.Print(printer);
  }
}

public record DocCommentBase(IIntelligentCommentContent Content, DocumentRange Range) : CommentBase(Range)
{
  public override void Print(PrettyPrinter printer)
  {
    base.Print(printer);
    Content.Print(printer);
  }
}

public record DocComment(
  IIntelligentCommentContent Content, DocumentRange Range) : DocCommentBase(Content, Range), IDocComment;

public record GroupOfLineComments(
  ITextContentSegment Text, DocumentRange Range) : CommentBase(Range), IGroupOfLineComments
{
  public override void Print(PrettyPrinter printer)
  {
    base.Print(printer);
    Text.Print(printer);
  }
}

public record MultilineComment(ITextContentSegment Text, DocumentRange Range) : CommentBase(Range), IMultilineComment
{
  public override void Print(PrettyPrinter printer)
  {
    base.Print(printer);
    Text.Print(printer);
  }
}

public record InlineReferenceComment(
  IInlineReferenceContentSegment Segment, DocumentRange Range) : CommentBase(Range), IInlineReferenceComment
{
  public override void Print(PrettyPrinter printer)
  {
    base.Print(printer);
    Segment.Print(printer);
  }
}


public record InlineCommentImpl(
  IEntityWithContentSegments Content, DocumentRange Range) : CommentBase(Range), IInlineComment
{
  public sealed override void Print(PrettyPrinter printer)
  {
    base.Print(printer);
    Content.Print(printer);
  }
}

public record InlineHackComment(
  IEntityWithContentSegments Content, DocumentRange Range) : InlineCommentImpl(Content, Range), IInlineHackComment;

public record InlineInvariantComment(
  IEntityWithContentSegments Content, DocumentRange Range) : InlineCommentImpl(Content, Range), IInlineInvariantComment;

public record InlineToDoComment(
  IEntityWithContentSegments Content, DocumentRange Range) : InlineCommentImpl(Content, Range), IInlineToDoComment;
