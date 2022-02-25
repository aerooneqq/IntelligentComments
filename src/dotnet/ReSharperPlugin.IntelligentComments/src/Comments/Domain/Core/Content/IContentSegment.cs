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
  [NotNull] IReference SourceReference { get; }
  [NotNull] IHighlightedText Description { get; }
}

public interface IToDo
{
  [NotNull] string Text { get; }
}

public interface IToDoContentSegment : IContentSegment
{
  [NotNull] IToDo ToDo { get; }
}

public interface IInvariantContentSegment : IContentSegment
{
  [NotNull] IHighlightedText Name { get; }
  [NotNull] IHighlightedText Description { get; }
}

public interface IReferenceContentSegment : IContentSegment
{
  [NotNull] IReference Reference { get; }
}