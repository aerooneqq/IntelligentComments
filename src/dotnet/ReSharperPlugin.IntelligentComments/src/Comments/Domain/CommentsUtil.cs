using System;
using System.Collections.Generic;
using System.Linq;
using JetBrains.Diagnostics;
using JetBrains.Rider.Model;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain;

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

  public static RdComment ToRdComment(this IDocComment docComment)
  {
    var content = docComment.Content.ToRdContent();
    var node = docComment.CommentOwnerPointer.GetTreeNode();
    Assertion.AssertNotNull(node, "node != null");

    var offset = node.GetTreeStartOffset().Offset;
    var authors = new List<RdIntelligentCommentAuthor> { new("Aero", DateTime.Now) };
    return new RdIntelligentComment(authors, DateTime.Now, content, null, null, null, null, offset);  
  }
  
  public static RdComment ToRdComment(this IIntelligentComment comment)
  {
    var content = comment.Content.ToRdContent();
    var node = comment.CommentOwnerPointer.GetTreeNode();
    Assertion.AssertNotNull(node, "node != null");

    var offset = node.GetTreeStartOffset().Offset;
    var authors = new List<RdIntelligentCommentAuthor> { new("Aero", DateTime.Now) };
    return new RdIntelligentComment(authors, DateTime.Now, content, null, null, null, null, offset);
  }

  public static RdIntelligentCommentContent ToRdContent(this IIntelligentCommentContent content)
  {
    var contentSegments = new List<RdContentSegment>();
    foreach (var contentSegment in content.ContentSegments.Segments)
      contentSegments.Add(contentSegment.ToRdContentSegment());

    return new RdIntelligentCommentContent(new RdContentSegments(contentSegments));
  }

  public static RdContentSegment ToRdContentSegment(this IContentSegment segment)
  {
    return segment switch
    {
      ITextContentSegment textSegment => new RdTextSegment(textSegment.Text.ToRdHighlightedText()),
      _ => throw new ArgumentOutOfRangeException(segment.GetType().Name)
    };
  }

  public static RdHighlightedText ToRdHighlightedText(this IHighlightedText text)
  {
    var rdHighlighters = text.Highlighters.Select(highlighter => highlighter.ToRdHighlighter()).ToList();
    return new RdHighlightedText(text.Text, rdHighlighters);
  }

  public static RdTextHighlighter ToRdHighlighter(this TextHighlighter highlighter)
  {
    var attributes = new RdTextAttributes();
    return new RdTextHighlighter(
      highlighter.Key,
      highlighter.StartOffset,
      highlighter.EndOffset,
      attributes);
  }
}