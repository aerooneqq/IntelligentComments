using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;

namespace IntelligentComments.Comments.Calculations.CodeHighlighting;

/// <summary>
/// When highlighting code in examples sections, we create a sandbox file and store the example code in this
/// sandbox file. The sandbox file can contain many examples.
/// In order to resolve references in future we need some abstraction to provide info about code example, and
/// <see cref="ISandBoxTreeNodeOperations"/> is this abstraction, it creates text for sandbox and finds the node for given range
/// </summary>
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