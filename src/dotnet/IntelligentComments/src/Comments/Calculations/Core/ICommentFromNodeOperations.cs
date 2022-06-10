using System.Collections.Generic;
using System.Linq;
using IntelligentComments.Comments.Domain.Core;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;

namespace IntelligentComments.Comments.Calculations.Core;

public record struct CommentCreationResult(
  [NotNull] ICommentBase Comment, 
  [NotNull] [ItemNotNull] IEnumerable<ITreeNode> CommentsNodes
);

/// <summary>
/// Interface which is used to find and create domain models of comments from ITreeNode. Moreover finds errors in the given node,
/// if this node is a comment. There are implementations for inline references, inline declarations, disabling comments.
/// </summary>
/// <todo name = "ICommentFromNodeOperationsCreateImplForDocComment">
/// <description>There is a need to integrate creation of domain models for doc comments into this infra</description>
/// </todo>
public interface ICommentFromNodeOperations
{
  int Priority { get; }
  
  CommentCreationResult? TryCreate([NotNull] ITreeNode node);
  [NotNull] [ItemNotNull] IEnumerable<CommentErrorHighlighting> FindErrors([NotNull] ITreeNode node);
}

/// <summary>
/// More specific type of operations, used for inline references and inline declarations. 
/// </summary>
public interface ISpecialGroupOfLinesCommentsOperations : ICommentFromNodeOperations
{
  bool CanBeStartOfSpecialGroupOfLineComments([NotNull] ITreeNode node);
}

public static class CommentFromNodeOperationsPriorities
{
  //reference to todo: ICommentFromNodeOperationsCreateImplForDocComment
  public const int DocComment = 4000;
  public const int MultilineComment = 3000;
  public const int DisablingComment = 2000;
  public const int Default = 1000;
  public const int Last = 0;
}

public static class CommentOperationsUtil
{
  /// <invariant name = "CollectSpecialOperationsMustOrderOperations">
  /// The operations must be ordered by priority
  /// </invariant>
  [NotNull]
  [ItemNotNull]
  public static IList<ISpecialGroupOfLinesCommentsOperations> CollectSpecialOperations([NotNull] ITreeNode context)
  {
    return LanguageManager.Instance
      .TryGetCachedServices<ISpecialGroupOfLinesCommentsOperations>(context.Language)
      .OrderByDescending(creator => creator.Priority)
      .ToList();
  }
  
  /// <invariant name = "CollectOperationsMustOrderOperations">
  /// The operations must be ordered by priority
  /// </invariant>
  [NotNull]
  [ItemNotNull]
  public static IList<ICommentFromNodeOperations> CollectOperations([NotNull] ITreeNode context)
  {
    return LanguageManager.Instance
      .TryGetCachedServices<ICommentFromNodeOperations>(context.Language)
      .OrderByDescending(operations => operations.Priority)
      .ToList();
  }
}