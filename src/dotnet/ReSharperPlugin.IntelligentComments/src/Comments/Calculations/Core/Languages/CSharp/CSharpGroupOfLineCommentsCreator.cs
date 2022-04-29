using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.Rd.Base;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CodeStyle;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Parsing;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments;
using ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.Languages.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpGroupOfLineCommentsCreator : GroupOfLineCommentsCreatorBase
{
  public override CommentCreationResult? TryCreate(ITreeNode node) => TryCreate(node, true);

  private CommentCreationResult? TryCreate(ITreeNode node, bool mergeDividedComments)
  {
    if (node is not ICSharpCommentNode startCommentNode) return null;
    if (!CanProcessLineComment(startCommentNode)) return null;
    
    var groupOfLineComments = CollectLineComments(startCommentNode, mergeDividedComments);
    var highlightedText = CreateTextFrom(groupOfLineComments);
    var comment = CreateCommentFrom(highlightedText, groupOfLineComments);
    return new CommentCreationResult(comment, groupOfLineComments);
  }

  public override CommentCreationResult? TryCreateNoMerge(ITreeNode commentNode) => TryCreate(commentNode, false);

  [NotNull]
  private static IHighlightedText CreateTextFrom(
    [NotNull] IReadOnlyList<ICSharpCommentNode> commentNodes)
  {
    if (commentNodes.Count == 0) return HighlightedText.CreateEmptyText();
    
    var texts = commentNodes.Select(comment => DocCommentsBuilderUtil.PreprocessText(comment.CommentText, null));
    var text = DocCommentsBuilderUtil.PreprocessText(string.Join("\n", texts), null);

    var highlightersProvider = LanguageManager.Instance.GetService<IHighlightersProvider>(commentNodes[0].Language);
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
    if (startCommentNode.TryFindDocCommentBlock() is { }) return false;
    
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
  private IReadOnlyList<ICSharpCommentNode> CollectLineComments(
    [NotNull] ICSharpCommentNode startCommentNode,
    bool mergeDividedComments)
  {
    var comments = new List<ICSharpCommentNode> { startCommentNode };
    var currentNode = startCommentNode.NextSibling;
    var manager = LanguageManager.Instance;
    var specialCommentsCreators = manager
      .TryGetCachedServices<ISpecialGroupOfLinesCommentsCreator>(startCommentNode.Language)
      .OrderByDescending(creator => creator.Priority)
      .ToList();

    while (currentNode is { })
    {
      if (currentNode.IsWhitespaceToken())
      {
        if (currentNode.NodeType == CSharpTokenType.NEW_LINE)
        {
          var node = currentNode.GetNextToken();
          while (node is { } && node.IsWhitespaceToken() && node.NodeType != CSharpTokenType.NEW_LINE)
            node = node.GetNextToken();

          if (!mergeDividedComments &&
              node is { } && node.NodeType == CSharpTokenType.NEW_LINE)
          {
            return comments;
          }
        }
        
        currentNode = currentNode.NextSibling;
        continue;
      }
      
      if (currentNode is ICSharpCommentNode { CommentType: CommentType.END_OF_LINE_COMMENT } commentNode)
      {
        var shouldAddCurrentNodeToGroup = true;
        foreach (var creator in specialCommentsCreators)
        {
          if (creator.CanBeStartOfSpecialGroupOfLineComments(commentNode))
          {
            shouldAddCurrentNodeToGroup = false;
            break;
          }
        }
        
        if (!shouldAddCurrentNodeToGroup) break;

        comments.Add(commentNode);
        currentNode = currentNode.NextSibling;
        continue;
      }

      break;
    }

    return comments;
  }
}