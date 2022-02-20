using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public record CommentProcessingResult(
  [NotNull] ICollection<HighlightingInfo> Errors,
  [CanBeNull] ICommentBase CommentBase)
{
  [NotNull] public static CommentProcessingResult CreateWithErrors(ICollection<HighlightingInfo> errors) => new(errors, null);

  [NotNull] public static CommentProcessingResult CreateWithoutErrors(ICommentBase comment) =>
    new(EmptyList<HighlightingInfo>.Instance, comment);
}

public class CommentsProcessor : IRecursiveElementProcessor
{
  [NotNull] [ItemNotNull] private readonly IList<CommentProcessingResult> myComments;
  [NotNull] [ItemNotNull] private readonly IList<ITreeNode> myVisitedComments;

  [NotNull] [ItemNotNull] public IReadOnlyList<CommentProcessingResult> Comments => myComments.AsIReadOnlyList();
  public bool ProcessingIsFinished => false;


  public CommentsProcessor()
  {
    myComments = new List<CommentProcessingResult>();
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
    myVisitedComments.Add(docCommentBlock);
    
    var errorsCollector = new CommentProblemsCollector(docCommentBlock);
    if (errorsCollector.Run() is { Count: > 0 } errors)
    {
      myComments.Add(CommentProcessingResult.CreateWithErrors(errors));
      return;
    }
    
    var builder = new DocCommentBuilder(docCommentBlock);

    if (builder.Build() is { } comment)
    {
      myComments.Add(CommentProcessingResult.CreateWithoutErrors(comment));
    }
  }

  private void ProcessLineComment([NotNull] ICSharpCommentNode commentNode)
  {
    var builder = new GroupOfLineCommentsBuilder(commentNode);

    myVisitedComments.Add(commentNode);

    if (builder.Build() is not var (groupOfLineComments, includedCommentsNodes)) return;
    
    myComments.Add(CommentProcessingResult.CreateWithoutErrors(groupOfLineComments));
    myVisitedComments.AddRange(includedCommentsNodes);
  }

  private void ProcessMultilineComment([NotNull] ICSharpCommentNode commentNode)
  {
    var builder = new MultilineCommentsBuilder(commentNode);
    
    myVisitedComments.Add(commentNode);
    
    if (builder.Build() is { } multilineComment)
    {
      myComments.Add(CommentProcessingResult.CreateWithoutErrors(multilineComment));
    }
  }

  public void ProcessAfterInterior(ITreeNode element)
  {
  }
}