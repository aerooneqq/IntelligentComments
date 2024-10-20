using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.Application.Parts;
using JetBrains.Application.Threading;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;

namespace IntelligentComments.Comments.Caches.Names.Entities;

[PsiComponent(Instantiation.DemandAnyThreadSafe)]
public class ToDoNamesCache : AbstractNamesCache
{
  public ToDoNamesCache(
    Lifetime lifetime,
    [NotNull] IShellLocks locks,
    [NotNull] ICommentsSettings settings,
    [NotNull] IPersistentIndexManager persistentIndexManager)
    : base(lifetime, NameKind.Todo, locks, settings, persistentIndexManager)
  {
  }

  protected override INamesProcessor TryGetProcessor(PsiLanguageType languageType)
  {
    return new NamesProcessor(NameKind.Todo);
  }
}