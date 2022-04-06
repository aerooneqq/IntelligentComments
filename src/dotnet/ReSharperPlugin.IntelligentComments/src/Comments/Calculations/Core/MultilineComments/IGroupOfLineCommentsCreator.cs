using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments;


public interface IGroupOfLineCommentsCreator : ICommentFromNodeCreator
{
}

public abstract class GroupOfLineCommentsCreatorBase : IGroupOfLineCommentsCreator
{
  public abstract CommentCreationResult? TryCreate(ITreeNode commentNode);
}