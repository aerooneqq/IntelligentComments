using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments;


public interface IGroupOfLineCommentsCreator : ICommentFromNodeCreator
{
}

public abstract class GroupOfLineCommentsCreatorBase : IGroupOfLineCommentsCreator
{
  public abstract CommentCreationResult? TryCreate(ITreeNode commentNode);
}