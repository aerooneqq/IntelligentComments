using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core
{
  public interface ICommentBase
  {
    [NotNull] ITreeNodePointer<ITreeNode> CommentOwnerPointer { get; }
  }

  public interface IDocComment : ICommentBase
  {
    [NotNull] IIntelligentCommentContent Content { get; }
  }

  public interface IIntelligentComment : ICommentBase
  {
    [NotNull] IIntelligentCommentContent Content { get; }
  }
}