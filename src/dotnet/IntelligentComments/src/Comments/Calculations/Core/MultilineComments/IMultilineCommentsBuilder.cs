using System.Collections.Generic;
using IntelligentComments.Comments.Domain.Core;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;

namespace IntelligentComments.Comments.Calculations.Core.MultilineComments;

public interface IMultilineCommentsBuilder : ICommentFromNodeOperations
{
}

public abstract class MultilineCommentBuilderBase : IMultilineCommentsBuilder
{
  [NotNull] protected const string Star = "*";

  public int Priority => CommentFromNodeOperationsPriorities.MultilineComment;
  
  public IEnumerable<CommentErrorHighlighting> FindErrors(ITreeNode node) => EmptyList<CommentErrorHighlighting>.Enumerable;

  public CommentCreationResult? TryCreate(ITreeNode node)
  {
    if (TryCreateInternal(node) is not { } multilineComment) return null;

    return new CommentCreationResult(multilineComment, new[] { node });
  }

  [CanBeNull] protected abstract IMultilineComment TryCreateInternal(ITreeNode node);
}