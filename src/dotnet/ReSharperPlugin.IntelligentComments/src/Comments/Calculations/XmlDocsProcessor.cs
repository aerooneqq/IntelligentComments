using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CodeStyle;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Builder;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public class XmlDocsProcessor : IRecursiveElementProcessor
{
  [NotNull] [ItemNotNull] private readonly IList<ICommentBase> myComments;
  [NotNull] [ItemNotNull] private readonly IList<ITreeNode> myVisitedComments;

  [NotNull] [ItemNotNull] public IReadOnlyList<ICommentBase> Comments => myComments.AsIReadOnlyList();


  public XmlDocsProcessor()
  {
    myComments = new List<ICommentBase>();
    myVisitedComments = new List<ITreeNode>();
  }


  public bool InteriorShouldBeProcessed(ITreeNode element) => true;

  public void ProcessBeforeInterior(ITreeNode element)
  {
    if (myVisitedComments.Contains(element)) return;

    switch (element)
    {
      case IDocCommentBlock docCommentBlock:
      {
        var builder = new DocCommentBuilder(docCommentBlock);

        if (builder.Build() is { } comment)
        {
          myComments.Add(comment);
        }

        myVisitedComments.Add(docCommentBlock);
        break;
      }
      case ICSharpCommentNode { CommentType: CommentType.END_OF_LINE_COMMENT } commentNode:
      {
        if (!CanProcessLineComment(commentNode)) return;
        
        var groupOfLineComments = CollectLineComments(commentNode);
        myVisitedComments.AddRange(groupOfLineComments);
        
        var text = string.Join("\n", groupOfLineComments.Select(comment => CommentsBuilderUtil.PreprocessText(comment.CommentText, null)));
        var startOffset = groupOfLineComments.First().GetDocumentRange().StartOffset;
        var endOffset = groupOfLineComments.Last().GetDocumentRange().EndOffset;
        var range = new DocumentRange(startOffset, endOffset);
        
        myComments.Add(new GroupOfLineComments(new TextContentSegment(text), range));

        break;
      }
    }
  }

  private static bool CanProcessLineComment([NotNull] ICSharpCommentNode commentNode)
  {
    var formatter = commentNode.GetCodeFormatter();
    var current = commentNode.PrevSibling;
    while (current is { })
    {
      if (!current.IsWhitespaceToken()) return false;
      if (formatter?.IsNewLine(current) ?? current.GetText() == "\n") return true;
      
      current = current.PrevSibling;
    }

    return true;
  }
  
  [NotNull]
  private static IReadOnlyList<ICSharpCommentNode> CollectLineComments([NotNull] ICSharpCommentNode firstComment)
  {
    var comments = new List<ICSharpCommentNode> { firstComment };
    var currentNode = firstComment.NextSibling;
    
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

  public void ProcessAfterInterior(ITreeNode element)
  {
  }

  public bool ProcessingIsFinished => false;
}