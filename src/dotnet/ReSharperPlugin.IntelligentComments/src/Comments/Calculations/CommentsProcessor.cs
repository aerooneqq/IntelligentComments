using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Builder;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public class CommentsProcessor : IRecursiveElementProcessor
{
  [NotNull] [ItemNotNull] private readonly IList<ICommentBase> myComments;
  [NotNull] [ItemNotNull] private readonly IList<ITreeNode> myVisitedComments;

  [NotNull] [ItemNotNull] public IReadOnlyList<ICommentBase> Comments => myComments.AsIReadOnlyList();


  public CommentsProcessor()
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
      case ICSharpDocCommentBlock docCommentBlock:
      {
        ProcessDocCommentBlock(docCommentBlock);
        break;
      }
      case ICSharpCommentNode { CommentType: CommentType.END_OF_LINE_COMMENT } commentNode:
      {
        ProcessLineComment(commentNode);
        break;
      }
      case ICSharpCommentNode { CommentType: CommentType.MULTILINE_COMMENT } commentNode:
      {
        ProcessMultilineComment(commentNode);
        break;
      }
    }
  }

  private void ProcessDocCommentBlock([NotNull] IDocCommentBlock docCommentBlock)
  {
    var builder = new DocCommentBuilder(docCommentBlock);

    if (builder.Build() is { } comment)
    {
      myComments.Add(comment);
    }

    myVisitedComments.Add(docCommentBlock);
  }

  private void ProcessLineComment([NotNull] ICSharpCommentNode commentNode)
  {
    var builder = new GroupOfLineCommentsBuilder(commentNode);

    myVisitedComments.Add(commentNode);
    
    if (builder.Build() is var (groupOfLineComments, includedCommentsNodes))
    {
      myComments.Add(groupOfLineComments);
      myVisitedComments.AddRange(includedCommentsNodes);
    }
  }

  private void ProcessMultilineComment([NotNull] ICSharpCommentNode commentNode)
  {
    var builder = new MultilineCommentsBuilder(commentNode);
    
    myVisitedComments.Add(commentNode);
    
    if (builder.Build() is { } multilineComment)
    {
      myComments.Add(multilineComment);
    }
  }

  public void ProcessAfterInterior(ITreeNode element)
  {
  }

  public bool ProcessingIsFinished => false;
}