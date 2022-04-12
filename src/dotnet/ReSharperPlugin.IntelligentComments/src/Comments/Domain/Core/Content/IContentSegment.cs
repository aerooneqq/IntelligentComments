using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Rider.Model;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

public interface IContentSegment
{
}

public interface IContentSegments
{
  [NotNull] IList<IContentSegment> Segments { get; }
}

public interface IEntityWithContentSegments : IContentSegment
{
  [NotNull] IContentSegments ContentSegments { get; }
}

public interface IRemarksSegment : IEntityWithContentSegments
{
}

public interface ISummarySegment : IEntityWithContentSegments
{
}

public interface IExceptionSegment : IEntityWithContentSegments
{
  [NotNull] IHighlightedText ExceptionName { get; }
}

public interface IExampleSegment : IEntityWithContentSegments
{
}

public interface IValueSegment : IEntityWithContentSegments
{
}

public interface IListSegment : IContentSegment
{
  ListKind ListKind { get; }
  [NotNull] IList<IListItem> Items { get; }
}

public interface IListItem
{
  [CanBeNull] IEntityWithContentSegments Header { get; }
  [CanBeNull] IEntityWithContentSegments Content { get; }
}

public interface ITableSegment : IContentSegment
{
  [CanBeNull] IHighlightedText Header { get; }
  [NotNull] IList<ITableSegmentRow> Rows { get; }
}

public interface ITableSegmentRow
{
  [NotNull] IList<ITableCell> Cells { get; }
}

public interface ITableCell
{
  [NotNull] IContentSegments Content { get; }
  [CanBeNull] TableCellProperties Properties { get; }
}

public record TableCellProperties(
  RdHorizontalAlignment HorizontalAlignment,
  RdVerticalAlignment VerticalAlignment,
  bool IsHeader)
{
  [NotNull] public static TableCellProperties DefaultProperties { get; } =
    new(RdHorizontalAlignment.Center, RdVerticalAlignment.Center, false);
}

public interface ICodeSegment : IContentSegment
{
  int HighlightingRequestId { get; }
  [NotNull] IHighlightedText Code { get; }
}

public interface IImageContentSegment : IContentSegment
{
  [NotNull] IDomainReference SourceDomainReference { get; }
  [NotNull] IHighlightedText Description { get; }
}

public interface IToDoTextContentSegment : IContentSegment
{
  [NotNull] IHighlightedText Text { get; }
}

public interface ITicketContentSegment : IContentSegment
{
  [NotNull] IEntityWithContentSegments Description { get; }
  [NotNull] IDomainReference Reference { get; }
}

public interface IToDoContentSegment : IContentSegmentWithOptionalName
{
  [NotNull] IEntityWithContentSegments Content { get; }
}

public interface IInlineHackContentSegment : IContentSegment
{
  [NotNull] IHighlightedText Text { get; }
}

public interface IInlineInvariantContentSegment : IContentSegment
{
  [NotNull] IHighlightedText Text { get; }
}

public interface IHackContentSegment : IContentSegmentWithOptionalName
{
  [NotNull] IEntityWithContentSegments Content { get; }
}

public interface IContentSegmentWithOptionalName : IContentSegment
{
  [CanBeNull] IHighlightedText Name { get; }
}

public interface IInvariantContentSegment : IContentSegment
{
  [NotNull] IHighlightedText Name { get; }
  [NotNull] IEntityWithContentSegments Description { get; }
}

public interface IReferenceContentSegment : IContentSegment
{
  [NotNull] IDomainReference DomainReference { get; }
  [NotNull] IHighlightedText Name { get; }
  [NotNull] IEntityWithContentSegments Description { get; }
}

public interface IInlineReferenceContentSegment : IContentSegment
{
  [NotNull] IHighlightedText NameText { get; }
  [CanBeNull] IHighlightedText DescriptionText { get; }
}