using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;

namespace IntelligentComments.Comments.Caches;

public interface ISandboxesCache
{
  [CanBeNull] IPsiSourceFile TryGetSandboxPsiSourceFile([NotNull] IDocument originalDocument, [NotNull] string fileName);
}