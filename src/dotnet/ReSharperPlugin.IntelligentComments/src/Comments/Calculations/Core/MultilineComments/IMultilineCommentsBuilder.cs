using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments;

public interface IMultilineCommentsBuilder : ICommentFromNodeCreator
{
}

public abstract class MultilineCommentBuilderBase : IMultilineCommentsBuilder
{
  [NotNull] protected const string Star = "*";

  public int Priority => CommentFromNodeCreatorsPriorities.MultilineComment;
  
  
  public CommentCreationResult? TryCreate(ITreeNode node)
  {
    if (TryCreateInternal(node) is not { } multilineComment) return null;

    return new CommentCreationResult(multilineComment, new[] { node });
  }

  [CanBeNull] protected abstract IMultilineComment TryCreateInternal(ITreeNode node);
}