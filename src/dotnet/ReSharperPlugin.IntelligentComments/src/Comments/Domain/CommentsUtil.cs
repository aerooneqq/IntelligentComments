using System;
using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.RdBackend.Common.Features.Util.Ranges;
using JetBrains.Rider.Model;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using FontStyle = ReSharperPlugin.IntelligentComments.Comments.Domain.Core.FontStyle;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain;

public static class CommentsUtil
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
      _ => throw new ArgumentOutOfRangeException(commentBase.GetType().Name)
    };
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
    RdIntelligentCommentContent content = docComment.Content.ToRdContent();
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
  private static RdContentSegment ToRdContentSegment([NotNull] this IContentSegment segment)
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
      _ => throw new ArgumentOutOfRangeException(segment.GetType().Name)
    };
  }
  
  [NotNull]
  private static RdTextInvariant ToRdInvariant([NotNull] this IInvariantContentSegment contentSegment)
  {
    return new RdTextInvariant(
      contentSegment.Name.ToRdHighlightedText(), contentSegment.Description.ToRdHighlightedText());
  }

  [NotNull]
  private static RdImageSegment ToRdImage([NotNull] this IImageContentSegment imageContentSegment)
  {
    return new RdImageSegment(
      imageContentSegment.SourceReference.ToRdReference(), imageContentSegment.Description.ToRdHighlightedText());
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
    return new RdTableCellProperties(properties.HorizontalAlignment, properties.VerticalAlignment, properties.IsHeader);
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
      seeAlso.Reference.ToRdReference(), seeAlso.HighlightedText.ToRdHighlightedText());
  }

  [NotNull]
  private static RdSeeAlsoMemberContentSegment ToRdSeeAlso([NotNull] this ISeeAlsoMemberContentSegment seeAlso)
  {
    return new RdSeeAlsoMemberContentSegment(
      seeAlso.Reference.ToRdReference(), seeAlso.HighlightedText.ToRdHighlightedText());
  }

  [NotNull]
  private static RdReference ToRdReference([NotNull] this IReference reference)
  {
    return reference switch
    {
      IProxyReference proxyReference => new RdProxyReference(proxyReference.RealReferenceId, string.Empty),
      ICodeEntityReference codeEntityReference => codeEntityReference.ToRdReference(),
      IExternalReference externalReference => externalReference.ToRdReference(),
      ILangWordReference langWordReference => langWordReference.ToRdReference(),
      IInvariantReference invariantReference => invariantReference.ToRdReference(),
      _ => throw new ArgumentOutOfRangeException(reference.GetType().Name),
    };
  }
  
  [NotNull]
  private static RdInvariantReference ToRdReference([NotNull] this IInvariantReference reference)
  {
    return new RdInvariantReference(reference.InvariantName, reference.InvariantName);
  }

  [NotNull]
  private static RdCodeEntityReference ToRdReference([NotNull] this ICodeEntityReference codeEntityReference)
  {
    return codeEntityReference switch
    {
      IXmlDocCodeEntityReference reference => new RdXmlDocCodeEntityReference(reference.RawValue),
      ISandBoxCodeEntityReference reference => new RdSandboxCodeEntityReference(
        reference.SandboxDocumentId,
        reference.OriginalDocument.GetData(DocumentHostBase.DocumentIdKey),
        reference.Range.ToRdTextRange(),
        reference.RawValue
      ),
      _ => throw new ArgumentOutOfRangeException(codeEntityReference.GetType().Name)
    };
  }

  [NotNull]
  private static RdExternalReference ToRdReference([NotNull] this IExternalReference externalReference)
  {
    return externalReference switch
    {
      IFileReference fileReference => fileReference.ToRdReference(),
      IHttpReference httpReference => httpReference.ToRdReference(),
      _ => throw new ArgumentOutOfRangeException(externalReference.GetType().Name)
    };
  }

  private static RdFileReference ToRdReference([NotNull] this IFileReference fileReference)
  {
    return new RdFileReference(fileReference.Path.FullPath, fileReference.RawValue);
  }

  [NotNull]
  private static RdLangWordReference ToRdReference([NotNull] this ILangWordReference langWordReference)
  {
    return new RdLangWordReference(langWordReference.RawValue);
  }

  [NotNull]
  private static RdHttpLinkReference ToRdReference([NotNull] this IHttpReference reference)
  {
    return new RdHttpLinkReference(reference.RawValue);
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
    List<RdTextHighlighter> rdHighlighters = text.Highlighters.Select(highlighter => highlighter.ToRdHighlighter()).ToList();
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
      isResharperHighlighter: highlighter.IsResharperHighlighter);
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
      _ => throw new ArgumentOutOfRangeException(textAnimation.GetType().Name)
    };
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
      RdContentSegments rdHeader = item.Header is { } header && header.ContentSegments.Segments.Count > 0
        ? item.Header.ContentSegments.ToRdContentSegments()
        : null;

      RdContentSegments rdDescription = item.Content is { } content && content.ContentSegments.Segments.Count > 0
        ? item.Content.ContentSegments.ToRdContentSegments()
        : null;

      items.Add(new RdListItem(rdHeader, rdDescription));
    }

    return new RdListSegment(listSegment.ListKind, items);
  }
}