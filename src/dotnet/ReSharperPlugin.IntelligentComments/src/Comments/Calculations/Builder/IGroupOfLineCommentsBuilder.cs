using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CodeStyle;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Builder;

public record struct GroupOfLineCommentsBuildResult(
  [NotNull] IGroupOfLineComments GroupOfLineComments,
  [NotNull] [ItemNotNull] IEnumerable<ICSharpCommentNode> CommentNodes);

public interface IGroupOfLineCommentsBuilder
{
  GroupOfLineCommentsBuildResult? Build();
}

public class GroupOfLineCommentsBuilder : IGroupOfLineCommentsBuilder
{
  [NotNull] private readonly ICSharpCommentNode myStartCommentNode;
  [NotNull] private readonly string myCommentAttributeId;

  [CanBeNull] private readonly IHighlightersProvider myHighlightersProvider;

  
  public GroupOfLineCommentsBuilder([NotNull] ICSharpCommentNode startCommentNode)
  {
    myStartCommentNode = startCommentNode;
    myHighlightersProvider = LanguageManager.Instance.GetService<IHighlightersProvider>(startCommentNode.Language);
    myCommentAttributeId = DefaultLanguageAttributeIds.DOC_COMMENT;
  }
  

  public GroupOfLineCommentsBuildResult? Build()
  {
    if (!CanProcessLineComment()) return null;
    
    var groupOfLineComments = CollectLineComments();
    var highlightedText = CreateTextFrom(groupOfLineComments);
    var comment = CreateCommentFrom(highlightedText, groupOfLineComments); 
      
    return new GroupOfLineCommentsBuildResult(comment, groupOfLineComments);
  }

  [NotNull]
  private IHighlightedText CreateTextFrom([NotNull] IEnumerable<ICSharpCommentNode> commentNodes)
  {
    var texts = commentNodes.Select(comment => CommentsBuilderUtil.PreprocessText(comment.CommentText, null));
    var text = string.Join("\n", texts);

    var highlighter = myHighlightersProvider?.TryGetReSharperHighlighter(myCommentAttributeId, text.Length);

    return new HighlightedText(text, highlighter);
  }

  [NotNull]
  private static IGroupOfLineComments CreateCommentFrom(
    [NotNull] IHighlightedText highlightedText,
    [NotNull] IEnumerable<ICSharpCommentNode> groupOfLineComments)
  {
    var startOffset = groupOfLineComments.First().GetDocumentRange().StartOffset;
    var endOffset = groupOfLineComments.Last().GetDocumentRange().EndOffset;
    var range = new DocumentRange(startOffset, endOffset);
    return new GroupOfLineComments(new TextContentSegment(highlightedText), range);
  }
  
  private bool CanProcessLineComment()
  {
    var formatter = myStartCommentNode.GetCodeFormatter();
    var current = myStartCommentNode.PrevSibling;
    while (current is { })
    {
      if (!current.IsWhitespaceToken()) return false;
      if (formatter?.IsNewLine(current) ?? current.GetText() == "\n") return true;
      
      current = current.PrevSibling;
    }

    return true;
  }

  [NotNull]
  private IReadOnlyList<ICSharpCommentNode> CollectLineComments()
  {
    var comments = new List<ICSharpCommentNode> { myStartCommentNode };
    var currentNode = myStartCommentNode.NextSibling;
    
    while (currentNode is { })
    {
      if (currentNode.IsWhitespaceToken())
      {
        currentNode = currentNode.NextSibling;
        continue;
      }
      
      if (currentNode is ICSharpCommentNode { CommentType: CommentType.END_OF_LINE_COMMENT } commentNode)
      {
        comments.Add(commentNode);
        currentNode = currentNode.NextSibling;
        continue;
      }

      break;
    }

    return comments;
  }
}