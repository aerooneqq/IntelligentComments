using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;

public record struct CommentCreationResult(
  [NotNull] ICommentBase Comment, 
  [NotNull] [ItemNotNull] IEnumerable<ITreeNode> CommentsNodes
);

public interface ICommentFromNodeCreator
{
  int Priority { get; }
  
  CommentCreationResult? TryCreate([NotNull] ITreeNode node);
}

public interface ISpecialGroupOfLinesCommentsCreator : ICommentFromNodeCreator
{
}

public static class CommentFromNodeCreatorsPriorities
{
  public const int DocComment = 4000;
  public const int MultilineComment = 3000;
  public const int DisablingComment = 2000;
  public const int Default = 1000;
  public const int Last = 0;
}