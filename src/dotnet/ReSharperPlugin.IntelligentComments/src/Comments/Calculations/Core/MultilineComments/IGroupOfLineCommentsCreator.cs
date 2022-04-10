using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments;


public interface IGroupOfLineCommentsCreator : ICommentFromNodeCreator
{
  CommentCreationResult? TryCreateNoMerge([NotNull] ITreeNode commentNode);
}

public abstract class GroupOfLineCommentsCreatorBase : IGroupOfLineCommentsCreator
{
  public abstract CommentCreationResult? TryCreate(ITreeNode commentNode);
  public abstract CommentCreationResult? TryCreateNoMerge(ITreeNode commentNode);
}