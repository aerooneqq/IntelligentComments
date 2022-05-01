using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Errors;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;

public record struct CommentCreationResult(
  [NotNull] ICommentBase Comment, 
  [NotNull] [ItemNotNull] IEnumerable<ITreeNode> CommentsNodes
);

public interface ICommentFromNodeOperations
{
  int Priority { get; }
  
  CommentCreationResult? TryCreate([NotNull] ITreeNode node);
  [NotNull] [ItemNotNull] IEnumerable<CommentErrorHighlighting> FindErrors([NotNull] ITreeNode node);
}

public interface ISpecialGroupOfLinesCommentsOperations : ICommentFromNodeOperations
{
  bool CanBeStartOfSpecialGroupOfLineComments([NotNull] ITreeNode node);
}

public static class CommentFromNodeOperationsPriorities
{
  public const int DocComment = 4000;
  public const int MultilineComment = 3000;
  public const int DisablingComment = 2000;
  public const int Default = 1000;
  public const int Last = 0;
}

public static class CommentOperationsUtil
{
  [NotNull]
  [ItemNotNull]
  public static IList<ISpecialGroupOfLinesCommentsOperations> CollectSpecialOperations([NotNull] ITreeNode context)
  {
    return LanguageManager.Instance
      .TryGetCachedServices<ISpecialGroupOfLinesCommentsOperations>(context.Language)
      .OrderByDescending(creator => creator.Priority)
      .ToList();
  }

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