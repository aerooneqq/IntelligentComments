using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Rd.Util;
using JetBrains.Rider.Model;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;


public record ContentSegments(IList<IContentSegment> Segments) : IContentSegments
{
  public static ContentSegments CreateEmpty() => new(new List<IContentSegment>());
  
  public void Print(PrettyPrinter printer)
  {
    using var _ = printer.IndentCookie();
    printer.Println($"{nameof(ContentSegments)}:");

    using var __ = printer.IndentCookie();
    foreach (var segment in Segments)
    {
      segment.Print(printer);
    }
  }
}
  
public class EntityWithContentSegments : IEntityWithContentSegments
{
  public IContentSegments ContentSegments { get; }


  public EntityWithContentSegments([NotNull] IContentSegments contentSegments)
  {
    ContentSegments = contentSegments;
  }

  public virtual void Print(PrettyPrinter printer)
  {
    using var _ = printer.IndentCookie();
    printer.Println($"{GetType().Name}:");
    
    ContentSegments.Print(printer);
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

public class ReturnContentSegment : EntityWithContentSegments, IReturnContentSegment
{
  public ReturnContentSegment(IContentSegments contentSegments) : base(contentSegments)
  {
  }
}

public class ParamContentSegment : EntityWithContentSegments, IParamContentSegment
{
  public IHighlightedText Name { get; }


  public ParamContentSegment(IHighlightedText name) : base(Content.ContentSegments.CreateEmpty())
  {
    Name = name;
  }
}

public class TypeParamSegment : ParamContentSegment, ITypeParamSegment
{
  public TypeParamSegment(IHighlightedText name) : base(name)
  {
  }
}

public class ParagraphContentSegment : EntityWithContentSegments, IParagraphContentSegment
{
  public ParagraphContentSegment(IContentSegments contentSegments) : base(contentSegments)
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

  public override void Print(PrettyPrinter printer)
  {
    using var _ = printer.IndentCookie();
    printer.Print($"Exception:");
    ExceptionName.Print(printer);
    
    base.Print(printer);
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

  
  public void Print(PrettyPrinter printer)
  {
    using var _ = printer.IndentCookie();
    printer.Print($"{GetType().Name}:");
    
    HighlightedText.Print(printer);
    DomainReference.Print(printer);
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

  
  public void Print(PrettyPrinter printer)
  {
    using var _ = printer.IndentCookie();
    printer.Print($"List {ListKind}:");  
    
    using var __ = printer.IndentCookie();
    foreach (var item in Items)
    {
      item.Print(printer);
    }
  }
}

public record ListItemImpl(IEntityWithContentSegments Header, IEntityWithContentSegments Content) : IListItem
{
  public void Print(PrettyPrinter printer)
  {
    printer.Println("Header:");
    using (printer.IndentCookie())
    {
      Header?.Print(printer);
    }
    
    printer.Println("Content:");
    using (printer.IndentCookie())
    {
      Content?.Print(printer);
    }
  }
}

public class TableSegment : ITableSegment
{
  public IHighlightedText Header { get; }
  public IList<ITableSegmentRow> Rows { get; }


  public TableSegment([CanBeNull] IHighlightedText header)
  {
    Header = header;
    Rows = new List<ITableSegmentRow>();
  }

  
  public void Print(PrettyPrinter printer)
  {
    printer.Println("Header:");
    using (printer.IndentCookie())
    {
      Header?.Print(printer);
    }
    
    using (printer.IndentCookie())
    {
      foreach (var row in Rows)
      {
        row.Print(printer);
      }
    }
  }
}

public class TableSegmentRow : ITableSegmentRow
{
  public IList<ITableCell> Cells { get; }


  public TableSegmentRow()
  {
    Cells = new List<ITableCell>();
  }

  public void Print(PrettyPrinter printer)
  {
    foreach (var cell in Cells)
    {
      cell.Print(printer);
    }
  }
}

public record TableCell(IContentSegments Content, TableCellProperties Properties) : ITableCell
{
  public void Print(PrettyPrinter printer)
  {
    printer.Print("Cell with properties: ");
    Properties?.Print(printer);
    Content.Print(printer);
  }
}

public record CodeSegment(IHighlightedText Code, int HighlightingRequestId) : ICodeSegment
{
  public void Print(PrettyPrinter printer)
  {
    printer.Println("Code Segment: ");
    using var _ = printer.IndentCookie();
    Code.Print(printer);
  }
}

public record ImageContentSegment(IDomainReference SourceDomainReference, IHighlightedText Description) : IImageContentSegment
{
  public void Print(PrettyPrinter printer)
  {
    printer.Println("Image: ");
    
    using var _ = printer.IndentCookie();
    Description.Print(printer);
    SourceDomainReference.Print(printer);
  }
}

public record InvariantContentSegment(
  IHighlightedText Name,
  IEntityWithContentSegments Description
) : IInvariantContentSegment
{
  public void Print(PrettyPrinter printer)
  {
    printer.Println("Invariant: ");
    
    using var _ = printer.IndentCookie();
    Name.Print(printer);
    Description.Print(printer);
  }
}

public record ReferenceContentSegment(
  IDomainReference DomainReference,
  IHighlightedText Name,
  IEntityWithContentSegments Description
) : IReferenceContentSegment
{
  public void Print(PrettyPrinter printer)
  {
    printer.Println("Reference: ");
    
    using var _ = printer.IndentCookie();
    Name.Print(printer);
    Description.Print(printer);
    DomainReference.Print(printer);
  }
}

public record InlineReferenceContentSegment(
  IHighlightedText NameText, 
  IHighlightedText DescriptionText
) : IInlineReferenceContentSegment
{
  public void Print(PrettyPrinter printer)
  {
    printer.Println("InlineReference: ");
    
    using var _ = printer.IndentCookie();
    NameText.Print(printer);
    printer.Println("Description: ");
    DescriptionText?.Print(printer);
  }
}

public record InlineContentSegment(IHighlightedText Name, IHighlightedText Text, NameKind NameKind) : IInlineContentSegment
{
  public void Print(PrettyPrinter printer)
  {
    printer.Println($"InlineContentSegment with kind {NameKind}: ");
    
    using var _ = printer.IndentCookie();
    Name?.Print(printer);
    Text?.Print(printer);
  }
}

public record ToDoContentSegment(IHighlightedText Name, IEntityWithContentSegments Content) : IToDoContentSegment
{
  public void Print(PrettyPrinter printer)
  {
    printer.Println($"ToDoContentSegment: ");
    
    using var _ = printer.IndentCookie();
    printer.Println("Name:");
    Name?.Print(printer);
    Content.Print(printer);
  }
}

public record TicketContentSegment(IEntityWithContentSegments Description, IDomainReference Reference) : ITicketContentSegment
{
  public void Print(PrettyPrinter printer)
  {
    printer.Println($"TicketContentSegment: ");
    
    using var _ = printer.IndentCookie();
    Description.Print(printer);
    Reference.Print(printer);
  }
}

public record HackContentSegment(IHighlightedText Name, IEntityWithContentSegments Content) : IHackContentSegment
{
  public void Print(PrettyPrinter printer)
  {
    printer.Println($"HackContentSegment: ");
    
    using var _ = printer.IndentCookie();
    printer.Println("Name:");
    Name?.Print(printer);
    Content.Print(printer);
  }
}