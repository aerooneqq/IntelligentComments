using IntelligentComments.Comments.Calculations.CodeHighlighting;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi.Files.SandboxFiles;

namespace IntelligentComments.Comments.Caches;

public interface ISandboxesCache
{
  [CanBeNull] SandboxPsiSourceFile TryGetSandboxPsiSourceFile([NotNull] IDocument originalDocument, string fileName);
}