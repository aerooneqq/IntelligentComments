using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Rider.Model;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;


public record ContentSegments(IList<IContentSegment> Segments) : IContentSegments
{
  public static ContentSegments CreateEmpty() => new(new List<IContentSegment>());
}
  
public class EntityWithContentSegments : IEntityWithContentSegments
{
  public IContentSegments ContentSegments { get; }


  public EntityWithContentSegments([NotNull] IContentSegments contentSegments)
  {
    ContentSegments = contentSegments;
  }
}

public class ValueSegment : EntityWithContentSegments, IValueSegment
{
  public ValueSegment([NotNull] IContentSegments contentSegments) : base(contentSegments)
  {
  }
}

public class RemarksContentSegment : EntityWithContentSegments, IRemarksSegment
{
  public RemarksContentSegment([NotNull] IContentSegments contentSegments) : base(contentSegments)
  {
  }
}

public class SummaryContentSegment : EntityWithContentSegments, ISummarySegment
{
  public SummaryContentSegment([NotNull] IContentSegments contentSegments) : base(contentSegments)
  {
  }
}

public class ExceptionContentSegment : EntityWithContentSegments, IExceptionSegment
{
  public IHighlightedText ExceptionName { get; }

  
  public ExceptionContentSegment([NotNull] IHighlightedText name) : base(Content.ContentSegments.CreateEmpty())
  {
    ExceptionName = name;
  }
}

public abstract class SeeAlsoContentSegment : ISeeAlsoContentSegment
{
  public IHighlightedText HighlightedText { get; }
  public IDomainReference DomainReference { get; }


  public SeeAlsoContentSegment([NotNull] IHighlightedText highlightedText, [NotNull] IDomainReference domainReference)
  {
    DomainReference = domainReference;
    HighlightedText = highlightedText;
  }
}

public class SeeAlsoMemberContentSegment : SeeAlsoContentSegment, ISeeAlsoMemberContentSegment 
{
  public SeeAlsoMemberContentSegment([NotNull] IHighlightedText highlightedText, [NotNull] IDomainReference domainReference) 
    : base(highlightedText, domainReference)
  {
  }
}

public class SeeAlsoLinkContentSegment : SeeAlsoContentSegment, ISeeAlsoLinkContentSegment
{
  public SeeAlsoLinkContentSegment([NotNull] IHighlightedText highlightedText, [NotNull] IExternalDomainReference domainReference) 
    : base(highlightedText, domainReference)
  {
  }
}

public class ExampleContentSegment : EntityWithContentSegments, IExampleSegment
{
  public ExampleContentSegment([NotNull] IContentSegments contentSegments) : base(contentSegments)
  {
  }
}

public class ListSegment : IListSegment
{
  public IList<IListItem> Items { get; }
  public ListKind ListKind { get; }


  public ListSegment(ListKind listKind)
  {
    ListKind = listKind;
    Items = new List<IListItem>();
  }
}

public record ListItemImpl(IEntityWithContentSegments Header, IEntityWithContentSegments Content) : IListItem;

public class TableSegment : ITableSegment
{
  public IHighlightedText Header { get; }
  public IList<ITableSegmentRow> Rows { get; }


  public TableSegment([CanBeNull] IHighlightedText header)
  {
    Header = header;
    Rows = new List<ITableSegmentRow>();
  }
}

public class TableSegmentRow : ITableSegmentRow
{
  public IList<ITableCell> Cells { get; }


  public TableSegmentRow()
  {
    Cells = new List<ITableCell>();
  }
}

public record TableCell(IContentSegments Content, TableCellProperties Properties) : ITableCell;

public record CodeSegment(IHighlightedText Code, int HighlightingRequestId) : ICodeSegment;

public record ImageContentSegment(IDomainReference SourceDomainReference, IHighlightedText Description) : IImageContentSegment;

public record InvariantContentSegment(
  IHighlightedText Name,
  IEntityWithContentSegments Description
) : IInvariantContentSegment;

public record ReferenceContentSegment(
  IDomainReference DomainReference,
  IHighlightedText Name,
  IEntityWithContentSegments Description
) : IReferenceContentSegment;

public record InlineReferenceContentSegment(
  IHighlightedText NameText, 
  IHighlightedText DescriptionText
) : IInlineReferenceContentSegment;

public record InlineContentSegment(IHighlightedText Name, IHighlightedText Text, NameKind NameKind) : IInlineContentSegment;

public record ToDoContentSegment(IHighlightedText Name, IEntityWithContentSegments Content) : IToDoContentSegment;

public record TicketContentSegment(
  IEntityWithContentSegments Description, 
  IDomainReference Reference
) : ITicketContentSegment;


public record HackContentSegment(IHighlightedText Name, IEntityWithContentSegments Content) : IHackContentSegment;