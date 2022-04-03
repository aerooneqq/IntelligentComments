using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CodeStyle;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.Languages.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpGroupOfLineCommentsBuilder : GroupOfLineCommentsBuilderBase
{
  public override GroupOfLineCommentsBuildResult? Build([NotNull] ICSharpCommentNode startCommentNode)
  {
    if (!CanProcessLineComment(startCommentNode)) return null;
    
    var groupOfLineComments = CollectLineComments(startCommentNode);
    var highlightedText = CreateTextFrom(startCommentNode, groupOfLineComments);
    var comment = CreateCommentFrom(highlightedText, groupOfLineComments);
    return new GroupOfLineCommentsBuildResult(comment, groupOfLineComments);
  }

  [NotNull]
  private IHighlightedText CreateTextFrom(
    [NotNull] ICSharpCommentNode startCommentNode,
    [NotNull] IReadOnlyList<ICSharpCommentNode> commentNodes)
  {
    var texts = commentNodes.Select(comment => CommentsBuilderUtil.PreprocessText(comment.CommentText, null));
    var text = CommentsBuilderUtil.PreprocessText(string.Join("\n", texts), null);

    var highlightersProvider = LanguageManager.Instance.GetService<IHighlightersProvider>(startCommentNode.Language);
    var highlighter = highlightersProvider?.TryGetDocCommentHighlighter(text.Length);

    return new HighlightedText(text, highlighter);
  }

  [NotNull]
  private static IGroupOfLineComments CreateCommentFrom(
    [NotNull] IHighlightedText highlightedText,
    [NotNull] IReadOnlyList<ICSharpCommentNode> groupOfLineComments)
  {
    var startOffset = groupOfLineComments.First().GetDocumentRange().StartOffset;
    var endOffset = groupOfLineComments.Last().GetDocumentRange().EndOffset;
    var range = new DocumentRange(startOffset, endOffset);
    return new GroupOfLineComments(new TextContentSegment(highlightedText), range);
  }
  
  private bool CanProcessLineComment([NotNull] ICSharpCommentNode startCommentNode)
  {
    var formatter = startCommentNode.GetCodeFormatter();
    var current = startCommentNode.PrevSibling;
    while (current is { })
    {
      if (!current.IsWhitespaceToken()) return false;
      if (formatter?.IsNewLine(current) ?? current.GetText() == "\n") return true;
      
      current = current.PrevSibling;
    }

    return true;
  }

  [NotNull]
  private IReadOnlyList<ICSharpCommentNode> CollectLineComments([NotNull] ICSharpCommentNode startCommentNode)
  {
    var comments = new List<ICSharpCommentNode> { startCommentNode };
    var currentNode = startCommentNode.NextSibling;
    
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