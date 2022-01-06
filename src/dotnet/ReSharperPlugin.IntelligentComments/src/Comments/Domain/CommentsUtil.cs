using System;
using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.RdBackend.Common.Features.Util.Ranges;
using JetBrains.ReSharper.Psi.Tree;
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
      IIntelligentComment intelligentComment => ToRdComment(intelligentComment),
      IDocComment docComment => ToRdComment(docComment),
      _ => throw new ArgumentOutOfRangeException(commentBase.GetType().Name)
    };
  }

  [NotNull]
  private static RdComment ToRdComment(this IDocComment docComment)
  {
    var content = docComment.Content.ToRdContent();
    var node = docComment.CommentOwnerPointer.GetTreeNode();
    Assertion.AssertNotNull(node, "node != null");

    var (startOffset, endOffset) = node.GetDocumentRange();
    var rdRange = new RdTextRange(startOffset.Offset, endOffset.Offset);
    return new RdDocComment(content, docComment.CreateIdentifier(), rdRange);
  }

  [NotNull]
  private static RdComment ToRdComment(this IIntelligentComment comment)
  {
    var content = comment.Content.ToRdContent();
    var node = comment.CommentOwnerPointer.GetTreeNode();
    Assertion.AssertNotNull(node, "node != null");

    var authors = new List<RdIntelligentCommentAuthor> { new("Aero", DateTime.Now) };
    var (startOffset, endOffset) = node.GetDocumentRange();
    var rdRange = new RdTextRange(startOffset.Offset, endOffset.Offset);
    var identifier = comment.CreateIdentifier();
      
    return new RdIntelligentComment(authors, DateTime.Now, content, null, null, null, null, identifier, rdRange);
  }

  [NotNull]
  private static RdIntelligentCommentContent ToRdContent(this IIntelligentCommentContent content)
  {
    return new RdIntelligentCommentContent(content.ContentSegments.ToRdContentSegments());
  }

  [NotNull]
  private static RdContentSegments ToRdContentSegments(this IContentSegments contentSegments)
  {
    var contentSegmentsList = new List<RdContentSegment>();
    foreach (var contentSegment in contentSegments.Segments)
    {
      contentSegmentsList.Add(contentSegment.ToRdContentSegment());
    }

    return new RdContentSegments(contentSegmentsList);
  }

  [NotNull]
  private static RdContentSegment ToRdContentSegment(this IContentSegment segment)
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
      _ => throw new ArgumentOutOfRangeException(segment.GetType().Name)
    };
  }

  private static RdCodeContentSegment ToRdCodeSegment(this ICodeSegment segment)
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
    return new RdSeeAlsoLinkContentSegment(seeAlso.Reference.ToRdReference(), seeAlso.HighlightedText.ToRdHighlightedText());
  }

  [NotNull]
  private static RdSeeAlsoMemberContentSegment ToRdSeeAlso([NotNull] this ISeeAlsoMemberContentSegment seeAlso)
  {
    return new RdSeeAlsoMemberContentSegment(seeAlso.Reference.ToRdReference(), seeAlso.HighlightedText.ToRdHighlightedText());
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
      _ => throw new ArgumentOutOfRangeException(reference.GetType().Name),
    };
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
      IHttpReference httpReference => httpReference.ToRdReference(),
      _ => throw new ArgumentOutOfRangeException(externalReference.GetType().Name)
    };
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
    return new RdExceptionsSegment(segment.ExceptionName.ToRdHighlightedText(), null, segment.ContentSegments.ToRdContentSegments());
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
      var header = item.Header.ContentSegments.Segments.Count > 0 ? item.Header.ContentSegments.ToRdContentSegments() : null;
      var description = item.Content.ContentSegments.Segments.Count > 0
        ? item.Content.ContentSegments.ToRdContentSegments()
        : null;
      
      items.Add(new RdListItem(header, description));
    }

    return new RdListSegment(listSegment.ListKind, items);
  }
}