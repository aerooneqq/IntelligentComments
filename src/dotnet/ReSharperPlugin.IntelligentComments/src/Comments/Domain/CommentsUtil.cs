using System;
using System.Collections.Generic;
using System.Linq;
using JetBrains.Diagnostics;
using JetBrains.Rider.Model;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using FontStyle = ReSharperPlugin.IntelligentComments.Comments.Domain.Core.FontStyle;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain
{
  public static class CommentsUtil
  {
    public static RdComment ToRdComment(this ICommentBase commentBase)
    {
      return commentBase switch
      {
        IIntelligentComment intelligentComment => ToRdComment(intelligentComment),
        IDocComment docComment => ToRdComment(docComment),
        _ => throw new ArgumentOutOfRangeException(commentBase.GetType().Name)
      };
    }

    private static RdComment ToRdComment(this IDocComment docComment)
    {
      var content = docComment.Content.ToRdContent();
      var node = docComment.CommentOwnerPointer.GetTreeNode();
      Assertion.AssertNotNull(node, "node != null");

      var offset = node.GetTreeStartOffset().Offset;
      return new RdDocComment(content, offset);
    }

    private static RdComment ToRdComment(this IIntelligentComment comment)
    {
      var content = comment.Content.ToRdContent();
      var node = comment.CommentOwnerPointer.GetTreeNode();
      Assertion.AssertNotNull(node, "node != null");

      var offset = node.GetTreeStartOffset().Offset;
      var authors = new List<RdIntelligentCommentAuthor> { new("Aero", DateTime.Now) };
      return new RdIntelligentComment(authors, DateTime.Now, content, null, null, null, null, offset);
    }

    private static RdIntelligentCommentContent ToRdContent(this IIntelligentCommentContent content)
    {
      return new RdIntelligentCommentContent(content.ContentSegments.ToRdContentSegments());
    }

    private static RdContentSegments ToRdContentSegments(this IContentSegments contentSegments)
    {
      var contentSegmentsList = new List<RdContentSegment>();
      foreach (var contentSegment in contentSegments.Segments)
      {
        contentSegmentsList.Add(contentSegment.ToRdContentSegment());
      }

      return new RdContentSegments(contentSegmentsList);
    }

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
        _ => throw new ArgumentOutOfRangeException(segment.GetType().Name)
      };
    }

    private static RdExceptionsSegment ToRdExceptionSegment(this IExceptionSegment segment)
    {
      return new RdExceptionsSegment(segment.ExceptionName, null, segment.ContentSegments.ToRdContentSegments());
    }
    
    private static RdRemarksSegment ToRdRemarks(this IRemarksSegment remarksSegment)
    {
      return new RdRemarksSegment(remarksSegment.ContentSegments.ToRdContentSegments());
    }
    
    private static RdReturnSegment ToRdReturn(this IReturnContentSegment returnContentSegment)
    {
      return new RdReturnSegment(returnContentSegment.ContentSegments.ToRdContentSegments());
    }
    
    private static RdParam ToRdParam(this IParamContentSegment param)
    {
      return new RdParam(param.Name, param.ContentSegments.ToRdContentSegments());
    }

    private static RdTextSegment ToRdTextSegment(this ITextContentSegment textContentSegment)
    {
      return new RdTextSegment(textContentSegment.Text.ToRdHighlightedText());
    }

    private static RdHighlightedText ToRdHighlightedText(this IHighlightedText text)
    {
      var rdHighlighters = text.Highlighters.Select(highlighter => highlighter.ToRdHighlighter()).ToList();
      return new RdHighlightedText(text.Text, rdHighlighters);
    }

    private static RdTextHighlighter ToRdHighlighter(this TextHighlighter highlighter)
    {
      var attributes = highlighter.Attributes.ToRdTextAttributes();
      return new RdTextHighlighter(
        highlighter.Key,
        highlighter.StartOffset,
        highlighter.EndOffset,
        attributes);
    }

    private static RdTextAttributes ToRdTextAttributes(this TextHighlighterAttributes attributes)
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

    private static RdParagraphSegment ToRdParagraph(this IParagraphContentSegment paragraph)
    {
      return new RdParagraphSegment(paragraph.ContentSegments.ToRdContentSegments());
    }
  }
}