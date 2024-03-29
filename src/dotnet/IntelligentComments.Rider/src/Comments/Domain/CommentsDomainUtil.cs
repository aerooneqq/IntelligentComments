using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Domain.Core;
using IntelligentComments.Comments.Domain.Core.Content;
using IntelligentComments.Comments.Domain.Core.References;
using JetBrains.Annotations;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.RdBackend.Common.Features.Util.Ranges;
using JetBrains.Rider.Model;
using FontStyle = IntelligentComments.Comments.Domain.Core.FontStyle;

namespace IntelligentComments.Rider.Comments.Domain;

public static class CommentsDomainUtil
{
  [NotNull]
  public static RdComment ToRdComment(this ICommentBase commentBase)
  {
    return commentBase switch
    {
      IDocComment docComment => ToRdComment(docComment),
      IGroupOfLineComments groupOfLineComments => groupOfLineComments.ToRdComment(),
      IMultilineComment multilineComment => multilineComment.ToRdComment(),
      IInvalidComment invalidComment => invalidComment.ToRdComment(),
      IDisablingComment disablingComment => disablingComment.ToRdComment(),
      IInlineReferenceComment comment => comment.ToRdComment(),
      IInlineToDoComment comment => comment.ToRdComment(),
      IInlineHackComment comment => comment.ToRdHackComment(),
      IInlineInvariantComment comment => comment.ToRdComment(),
      _ => throw new ArgumentOutOfRangeException(commentBase.GetType().Name)
    };
  }
  
  [NotNull]
  private static RdInlineInvariantComment ToRdComment([NotNull] this IInlineInvariantComment comment)
  {
    return new RdInlineInvariantComment(comment.Content.ToRdContentSegment(), comment.GetRdRange());
  }

  [NotNull]
  private static RdInlineToDoComment ToRdComment([NotNull] this IInlineToDoComment inlineToDoComment)
  {
    return new RdInlineToDoComment(inlineToDoComment.Content.ToRdContentSegment(), inlineToDoComment.GetRdRange());
  }

  [NotNull]
  private static RdInlineHackComment ToRdHackComment([NotNull] this IInlineHackComment comment)
  {
    return new RdInlineHackComment(comment.Content.ToRdContentSegment(), comment.GetRdRange());
  }

  [NotNull]
  private static RdInlineReferenceComment ToRdComment([NotNull] this IInlineReferenceComment comment)
  {
    return new RdInlineReferenceComment(comment.Segment.ToRdSegment(), comment.GetRdRange());
  }

  [NotNull]
  private static RdDisableInspectionComment ToRdComment([NotNull] this IDisablingComment disablingComment)
  {
    return new RdDisableInspectionComment(
      disablingComment.DisabledInspections.ToRdTextSegment(), disablingComment.GetRdRange());
  }
  
  [NotNull]
  private static RdInvalidComment ToRdComment([NotNull] this IInvalidComment invalidComment)
  {
    return new RdInvalidComment(invalidComment.ErrorsSummary.ToRdTextSegment(), invalidComment.GetRdRange());
  }

  [NotNull]
  private static RdMultilineComment ToRdComment([NotNull] this IMultilineComment multilineComment)
  {
    return new RdMultilineComment(multilineComment.Text.ToRdTextSegment(), multilineComment.GetRdRange());
  }

  [NotNull]
  private static RdGroupOfLineComments ToRdComment([NotNull] this IGroupOfLineComments groupOfLineComments)
  {
    return new RdGroupOfLineComments(groupOfLineComments.Text.ToRdTextSegment(), groupOfLineComments.GetRdRange());
  }
  
  [NotNull]
  private static RdTextRange GetRdRange([NotNull] this ICommentBase comment)
  {
    var (startOffset, endOffset) = comment.Range;
    return new RdTextRange(startOffset.Offset, endOffset.Offset);
  }

  [NotNull]
  private static RdComment ToRdComment([NotNull] this IDocComment docComment)
  {
    var content = docComment.Content.ToRdContent();
    return new RdDocComment(content, docComment.GetRdRange());
  }
  
  [NotNull]
  private static RdIntelligentCommentContent ToRdContent([NotNull] this IIntelligentCommentContent content)
  {
    return new RdIntelligentCommentContent(content.ContentSegments.ToRdContentSegments());
  }

  [NotNull]
  private static RdContentSegments ToRdContentSegments([NotNull] this IContentSegments contentSegments)
  {
    var contentSegmentsList = new List<RdContentSegment>();
    foreach (var contentSegment in contentSegments.Segments)
    {
      contentSegmentsList.Add(contentSegment.ToRdContentSegment());
    }

    return new RdContentSegments(contentSegmentsList);
  }

  [NotNull]
  internal static RdContentSegment ToRdContentSegment([NotNull] this IContentSegment segment)
  {
    return segment switch
    {
      ITextContentSegment textSegment => textSegment.ToRdTextSegment(),
      IParagraphContentSegment paragraph => paragraph.ToRdParagraph(),
      IParamContentSegment param => param.ToRdParam(),
      IReturnContentSegment @return => @return.ToRdReturn(),
      IRemarksSegment remarks => remarks.ToRdRemarks(),
      IExceptionSegment exceptionSegment => exceptionSegment.ToRdExceptionSegment(),
      ISeeAlsoContentSegment seeAlsoSegment => seeAlsoSegment.ToRdSeeAlso(),
      IExampleSegment exampleSegment => exampleSegment.ToRdExample(),
      IListSegment listSegment => listSegment.ToRdList(),
      ISummarySegment summarySegment => summarySegment.ToRdSummary(),
      ITableSegment tableSegment => tableSegment.ToRdTable(),
      ICodeSegment codeSegment => codeSegment.ToRdCodeSegment(),
      IValueSegment valueSegment => valueSegment.ToRdValue(),
      IImageContentSegment imageContentSegment => imageContentSegment.ToRdImage(),
      IInvariantContentSegment contentSegment => contentSegment.ToRdInvariant(),
      IReferenceContentSegment contentSegment => contentSegment.ToRdReferenceSegment(),
      IInlineReferenceContentSegment contentSegment => contentSegment.ToRdSegment(),
      ITicketContentSegment contentSegment => contentSegment.ToRdTicket(),
      IToDoContentSegment contentSegment => contentSegment.ToRdContentSegment(),
      IHackContentSegment contentSegment => contentSegment.ToRdContentSegment(),
      IInlineContentSegment contentSegment => contentSegment.ToRdContentSegment(),
      IEntityWithContentSegments contentSegment => contentSegment.ToRdContentSegment(),
      _ => throw new ArgumentOutOfRangeException(segment.GetType().Name)
    };
  }
  
  [NotNull]
  private static RdInlineContentSegment ToRdContentSegment([NotNull] this IInlineContentSegment segment)
  {
    return new RdInlineContentSegment(
      segment.Name?.ToRdHighlightedText(), segment.NameKind.ToRdNameKind(), segment.Text.ToRdHighlightedText());
  }

  [NotNull]
  private static RdHackContentSegment ToRdContentSegment([NotNull] this IHackContentSegment segment)
  {
    return new RdHackContentSegment(segment.Content.ToRdContentSegment(), segment.Name?.ToRdHighlightedText());
  }

  [NotNull]
  private static RdTicketContentSegment ToRdTicket([NotNull] this ITicketContentSegment segment)
  {
    return new RdTicketContentSegment(segment.Reference.ToRdReference(), segment.Content.ToRdContentSegment());
  }

  [NotNull]
  private static RdToDoContentSegment ToRdContentSegment([NotNull] this IToDoContentSegment segment)
  {
    return new RdToDoContentSegment(segment.Content.ToRdContentSegment(), segment.Name?.ToRdHighlightedText());
  }
  
  [NotNull]
  private static RdReferenceContentSegment ToRdReferenceSegment([NotNull] this IReferenceContentSegment contentSegment)
  {
    return new RdReferenceContentSegment(
      contentSegment.DomainReference.ToRdReference(),
      contentSegment.Name.ToRdHighlightedText(),
      contentSegment.Description.ToRdContentSegment()
    );
  }
  
  [NotNull]
  public static RdDefaultSegmentWithContent ToRdContentSegment([NotNull] this IEntityWithContentSegments entity)
  {
    return new RdDefaultSegmentWithContent(entity.ContentSegments.ToRdContentSegments());
  }
  
  [NotNull]
  public static RdTextInvariant ToRdInvariant([NotNull] this IInvariantContentSegment contentSegment)
  {
    return new RdTextInvariant(contentSegment.Description.ToRdHighlightedText(), contentSegment?.Name?.ToRdHighlightedText());
  }

  [NotNull]
  private static RdImageSegment ToRdImage([NotNull] this IImageContentSegment imageContentSegment)
  {
    return new RdImageSegment(
      imageContentSegment.SourceDomainReference.ToRdReference(), imageContentSegment.Description.ToRdHighlightedText());
  }
  
  [NotNull]
  private static RdValueSegment ToRdValue([NotNull] this IValueSegment valueSegment)
  {
    return new RdValueSegment(valueSegment.ContentSegments.ToRdContentSegments());
  }

  [NotNull]
  private static RdCodeContentSegment ToRdCodeSegment([NotNull] this ICodeSegment segment)
  {
    return new RdCodeContentSegment(segment.Code.ToRdHighlightedText(), segment.HighlightingRequestId);
  }

  [NotNull]
  private static RdSummarySegment ToRdSummary([NotNull] this ISummarySegment summarySegment)
  {
    return new RdSummarySegment(summarySegment.ContentSegments.ToRdContentSegments());
  }

  [NotNull]
  private static RdTableSegment ToRdTable([NotNull] this ITableSegment table)
  {
    var rows = new List<RdTableRow>();
    foreach (var row in table.Rows)
    {
      var cells = new List<RdTableCell>();
      foreach (var cell in row.Cells)
      {
        cells.Add(new RdTableCell(cell.Content.ToRdContentSegments(), cell.Properties?.ToRdTableCellProperties()));
      }

      rows.Add(new RdTableRow(cells));
    }

    return new RdTableSegment(rows);
  }

  [NotNull]
  private static RdTableCellProperties ToRdTableCellProperties([NotNull] this TableCellProperties properties)
  {
    return new RdTableCellProperties(
      properties.HorizontalAlignment.ToRdHorizontalAlignment(), 
      properties.VerticalAlignment.ToRdVerticalAlignment(),
      properties.IsHeader);
  }

  private static RdHorizontalAlignment ToRdHorizontalAlignment(this HorizontalAlignment horizontalAlignment)
  {
    return horizontalAlignment switch
    {
      HorizontalAlignment.Center => RdHorizontalAlignment.Center,
      HorizontalAlignment.Left => RdHorizontalAlignment.Left,
      HorizontalAlignment.Right => RdHorizontalAlignment.Right,
      _ => throw new ArgumentOutOfRangeException(horizontalAlignment.ToString())
    };
  }

  private static RdVerticalAlignment ToRdVerticalAlignment(this VerticalAlignment verticalAlignment)
  {
    return verticalAlignment switch
    {
      VerticalAlignment.Top => RdVerticalAlignment.Top,
      VerticalAlignment.Bottom => RdVerticalAlignment.Bottom,
      VerticalAlignment.Center => RdVerticalAlignment.Center,
      _ => throw new ArgumentOutOfRangeException(verticalAlignment.ToString())
    };
  }

  [NotNull]
  private static RdExampleSegment ToRdExample([NotNull] this IExampleSegment exampleSegment)
  {
    return new RdExampleSegment(exampleSegment.ContentSegments.ToRdContentSegments());
  }

  [NotNull]
  private static RdSeeAlsoContentSegment ToRdSeeAlso([NotNull] this ISeeAlsoContentSegment seeAlsoContentSegment)
  {
    return seeAlsoContentSegment switch
    {
      ISeeAlsoLinkContentSegment seeAlso => seeAlso.ToRdSeeAlso(),
      ISeeAlsoMemberContentSegment seeAlso => seeAlso.ToRdSeeAlso(),
      _ => throw new ArgumentOutOfRangeException(seeAlsoContentSegment.GetType().Name)
    };
  }

  [NotNull]
  private static RdSeeAlsoLinkContentSegment ToRdSeeAlso([NotNull] this ISeeAlsoLinkContentSegment seeAlso)
  {
    return new RdSeeAlsoLinkContentSegment(
      seeAlso.DomainReference.ToRdReference(), seeAlso.HighlightedText.ToRdHighlightedText());
  }

  [NotNull]
  private static RdSeeAlsoMemberContentSegment ToRdSeeAlso([NotNull] this ISeeAlsoMemberContentSegment seeAlso)
  {
    return new RdSeeAlsoMemberContentSegment(
      seeAlso.DomainReference.ToRdReference(), seeAlso.HighlightedText.ToRdHighlightedText());
  }

  [NotNull]
  private static RdInlineReferenceContentSegment ToRdSegment([NotNull] this IInlineReferenceContentSegment segment)
  {
    return new RdInlineReferenceContentSegment(
      segment.NameText.ToRdHighlightedText(), segment.DescriptionText?.ToRdHighlightedText());
  }

  [NotNull]
  private static RdReference ToRdReference([NotNull] this IDomainReference domainReference)
  {
    return domainReference switch
    {
      IProxyDomainReference proxyReference => new RdProxyReference(proxyReference.RealReferenceId, string.Empty),
      ICodeEntityDomainReference codeEntityReference => codeEntityReference.ToRdReference(),
      IExternalDomainReference externalReference => externalReference.ToRdReference(),
      ILangWordDomainReference langWordReference => langWordReference.ToRdReference(),
      INamedEntityDomainReference invariantReference => invariantReference.ToRdReference(),
      _ => throw new ArgumentOutOfRangeException(domainReference.GetType().Name),
    };
  }

  [NotNull]
  public static RdNamedEntityReference ToRdReference([NotNull] this INamedEntityDomainReference reference)
  {
    return new RdNamedEntityReference(reference.NameKind.ToRdNameKind(), reference.Name, reference.Name);
  }

  public static RdNameKind ToRdNameKind(this NameKind nameKind) => nameKind switch
  {
    NameKind.Invariant => RdNameKind.Invariant,
    NameKind.Hack => RdNameKind.Hack,
    NameKind.Todo => RdNameKind.Todo,
    _ => throw new ArgumentOutOfRangeException(nameKind.ToString())
  };

  [NotNull]
  private static RdCodeEntityReference ToRdReference([NotNull] this ICodeEntityDomainReference codeEntityDomainReference)
  {
    return codeEntityDomainReference switch
    {
      IXmlDocCodeEntityDomainReference reference => new RdXmlDocCodeEntityReference(reference.RawValue),
      ISandBoxCodeEntityDomainReference reference => new RdSandboxCodeEntityReference(
        reference.SandboxDocumentId,
        reference.OriginalDocument.GetProtocolSynchronizer().DocumentId,
        reference.Range.ToRdTextRange(),
        reference.RawValue
      ),
      _ => throw new ArgumentOutOfRangeException(codeEntityDomainReference.GetType().Name)
    };
  }

  [NotNull]
  private static RdExternalReference ToRdReference([NotNull] this IExternalDomainReference externalDomainReference)
  {
    return externalDomainReference switch
    {
      IFileDomainReference fileReference => fileReference.ToRdReference(),
      IHttpDomainReference httpReference => httpReference.ToRdReference(),
      _ => throw new ArgumentOutOfRangeException(externalDomainReference.GetType().Name)
    };
  }

  private static RdFileReference ToRdReference([NotNull] this IFileDomainReference fileDomainReference)
  {
    return new RdFileReference(fileDomainReference.Path.FullPath, fileDomainReference.RawValue);
  }

  [NotNull]
  private static RdLangWordReference ToRdReference([NotNull] this ILangWordDomainReference langWordDomainReference)
  {
    return new RdLangWordReference(langWordDomainReference.RawValue);
  }

  [NotNull]
  private static RdHttpLinkReference ToRdReference([NotNull] this IHttpDomainReference domainReference)
  {
    return new RdHttpLinkReference(domainReference.DisplayName, domainReference.RawValue);
  }

  [NotNull]
  private static RdExceptionsSegment ToRdExceptionSegment([NotNull] this IExceptionSegment segment)
  {
    return new RdExceptionsSegment(segment.ExceptionName.ToRdHighlightedText(), null,
      segment.ContentSegments.ToRdContentSegments());
  }

  [NotNull]
  private static RdRemarksSegment ToRdRemarks([NotNull] this IRemarksSegment remarksSegment)
  {
    return new RdRemarksSegment(remarksSegment.ContentSegments.ToRdContentSegments());
  }

  [NotNull]
  private static RdReturnSegment ToRdReturn([NotNull] this IReturnContentSegment returnContentSegment)
  {
    return new RdReturnSegment(returnContentSegment.ContentSegments.ToRdContentSegments());
  }

  [NotNull]
  private static RdParam ToRdParam([NotNull] this IParamContentSegment param)
  {
    return param switch
    {
      ITypeParamSegment paramSegment => paramSegment.ToRdParam(),
      { } => new RdParam(param.Name.ToRdHighlightedText(), param.ContentSegments.ToRdContentSegments()),
    };
  }

  [NotNull]
  private static RdTextSegment ToRdTextSegment([NotNull] this ITextContentSegment textContentSegment)
  {
    return new RdTextSegment(textContentSegment.Text.ToRdHighlightedText());
  }

  [NotNull]
  public static RdHighlightedText ToRdHighlightedText([NotNull] this IHighlightedText text)
  {
    var rdHighlighters = text.Highlighters.Select(highlighter => highlighter.ToRdHighlighter()).ToList();
    return new RdHighlightedText(text.Text, rdHighlighters);
  }

  [NotNull]
  private static RdTextHighlighter ToRdHighlighter([NotNull] this TextHighlighter highlighter)
  {
    var attributes = highlighter.Attributes.ToRdTextAttributes();
    return new RdTextHighlighter(
      highlighter.Key,
      highlighter.StartOffset,
      highlighter.EndOffset,
      attributes,
      references: highlighter.References?.Select(reference => reference.ToRdReference()).ToList(),
      animation: highlighter.TextAnimation?.ToRdAnimation(),
      isResharperHighlighter: highlighter.IsResharperHighlighter,
      errorSquiggles: highlighter.ErrorSquiggles?.ToRdSquiggles());
  }
  
  public static RdSquiggles ToRdSquiggles(this Squiggles squiggles)
  {
    return new RdSquiggles(squiggles.Kind.ToRdSquigglesKind(), squiggles.ColorKey);
  }
  
  public static RdSquigglesKind ToRdSquigglesKind(this SquigglesKind kind)
  {
    return kind switch
    {
      SquigglesKind.Dotted => RdSquigglesKind.Dotted,
      SquigglesKind.Wave => RdSquigglesKind.Wave,
      _ => throw new ArgumentOutOfRangeException(kind.ToString())
    };
  }

  [NotNull]
  private static RdTextAttributes ToRdTextAttributes([NotNull] this TextHighlighterAttributes attributes)
  {
    var rdAttributes = attributes.FontStyle.ToRdFontStyle();
    return new RdTextAttributes(rdAttributes, attributes.Underline, (float)attributes.FontWeight);
  }

  private static RdFontStyle ToRdFontStyle(this FontStyle fontStyle) =>
    fontStyle switch
    {
      FontStyle.Bold => RdFontStyle.Bold,
      FontStyle.Regular => RdFontStyle.Regular,
      FontStyle.Italic => RdFontStyle.Italic,
      _ => throw new ArgumentOutOfRangeException(nameof(fontStyle))
    };

  [NotNull]
  private static RdTextAnimation ToRdAnimation([NotNull] this TextAnimation textAnimation)
  {
    return textAnimation switch
    {
      UnderlineTextAnimation => new RdUnderlineTextAnimation(),
      ForegroundTextAnimation animation => new RdForegroundColorAnimation(animation.HoveredColor.ToRdColor()),
      _ => throw new ArgumentOutOfRangeException(textAnimation.GetType().Name)
    };
  }

  private static RdColor ToRdColor(this Color color)
  {
    return new RdColor("#" + color.R.ToString("X2") + color.G.ToString("X2") + color.B.ToString("X2"));
  }

  [NotNull]
  private static RdParagraphSegment ToRdParagraph([NotNull] this IParagraphContentSegment paragraph)
  {
    return new RdParagraphSegment(paragraph.ContentSegments.ToRdContentSegments());
  }

  [NotNull]
  private static RdTypeParam ToRdParam([NotNull] this ITypeParamSegment typeParamSegment)
  {
    return new RdTypeParam(
      typeParamSegment.Name.ToRdHighlightedText(), typeParamSegment.ContentSegments.ToRdContentSegments());
  }

  [NotNull]
  private static RdListSegment ToRdList([NotNull] this IListSegment listSegment)
  {
    var items = new List<RdListItem>();
    foreach (var item in listSegment.Items)
    {
      var rdHeader = item.Header is { } header && header.ContentSegments.Segments.Count > 0
        ? item.Header.ContentSegments.ToRdContentSegments()
        : null;

      var rdDescription = item.Content is { } content && content.ContentSegments.Segments.Count > 0
        ? item.Content.ContentSegments.ToRdContentSegments()
        : null;

      items.Add(new RdListItem(rdHeader, rdDescription));
    }

    return new RdListSegment(listSegment.ListKind.ToRdKind(), items);
  }

  private static RdListKind ToRdKind(this ListKind listKind)
  {
    return listKind switch
    {
      ListKind.Bullet => RdListKind.Bullet,
      ListKind.Number => RdListKind.Number,
      _ => throw new ArgumentOutOfRangeException(listKind.ToString())
    };
  }
}