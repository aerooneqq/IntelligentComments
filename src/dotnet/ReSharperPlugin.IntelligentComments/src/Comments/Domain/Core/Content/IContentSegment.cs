using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Rider.Model;

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
  [NotNull] public IContentSegments ContentSegments { get; }
}

public interface IRemarksSegment : IEntityWithContentSegments
{
}

public interface IExceptionSegment : IEntityWithContentSegments
{
  string ExceptionName { get; }
}

public interface IExampleSegment : IEntityWithContentSegments
{
}

public interface IListSegment : IContentSegment
{
  public ListKind ListKind { get; }
  [NotNull] IList<IListItem> Items { get; }
}

public interface IListItem
{
  IEntityWithContentSegments Header { get; }
  IEntityWithContentSegments Content { get; }
}

public interface ITableSegment : IContentSegment
{
  [CanBeNull] public IHighlightedText Header { get; }
  [NotNull] public IList<ITableSegmentRow> Rows { get; }
}

public interface ITableSegmentRow
{
  [NotNull] public IList<ITableCell> Cells { get; }
}

public interface ITableCell
{
  [NotNull] public IContentSegments Content { get; }
  [CanBeNull] public TableCellProperties Properties { get; }
}

public record TableCellProperties(
  RdHorizontalAlignment HorizontalAlignment,
  RdVerticalAlignment VerticalAlignment,
  bool IsHeader)
{
  public static TableCellProperties DefaultProperties { get; } =
    new(RdHorizontalAlignment.Center, RdVerticalAlignment.Center, false);
}