using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;

namespace IntelligentComments.Comments.Calculations.Core.MultilineComments;

public interface IGroupOfLineCommentsOperations : ICommentFromNodeOperations
{
  CommentCreationResult? TryCreateNoMerge([NotNull] ITreeNode commentNode);
}

public abstract class GroupOfLineCommentsOperationsBase : IGroupOfLineCommentsOperations
{
  public int Priority => CommentFromNodeOperationsPriorities.Last;

  public IEnumerable<CommentErrorHighlighting> FindErrors(ITreeNode node) => EmptyList<CommentErrorHighlighting>.Enumerable;

  public abstract CommentCreationResult? TryCreate(ITreeNode commentNode);
  public abstract CommentCreationResult? TryCreateNoMerge(ITreeNode commentNode);
}