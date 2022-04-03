using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments;

public record struct GroupOfLineCommentsBuildResult(
  [NotNull] IGroupOfLineComments GroupOfLineComments,
  [NotNull] [ItemNotNull] IEnumerable<ICSharpCommentNode> CommentNodes);

public interface IGroupOfLineCommentsBuilder
{
  GroupOfLineCommentsBuildResult? Build([NotNull] ITreeNode commentNode);
}

public abstract class GroupOfLineCommentsBuilderBase : IGroupOfLineCommentsBuilder
{
  public abstract GroupOfLineCommentsBuildResult? Build(ITreeNode commentNode);
}