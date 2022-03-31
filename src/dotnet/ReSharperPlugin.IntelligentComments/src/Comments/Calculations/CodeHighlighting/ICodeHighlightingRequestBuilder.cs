using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public interface ISandBoxTreeNodeOperations
{
  [CanBeNull] string CreateTextForSandBox();
  [CanBeNull] ITreeNode TryFind([NotNull] IFile file, TreeTextRange range);
}

public record struct SandBoxNodeCreationResult([NotNull] ITreeNode Node, [NotNull] ISandBoxTreeNodeOperations NodeOperations);

public interface ICodeHighlightingRequestBuilder
{
  [CanBeNull] 
  SandBoxNodeCreationResult? CreateNodeOperations([NotNull] string rawCodeText, [NotNull] ITreeNode commentOwner);
}