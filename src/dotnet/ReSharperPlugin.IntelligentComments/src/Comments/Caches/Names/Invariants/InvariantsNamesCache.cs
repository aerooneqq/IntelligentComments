using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Names.Invariants;

[PsiComponent]
public class InvariantsNamesNamesCache : AbstractNamesCache
{
  public InvariantsNamesNamesCache(
    Lifetime lifetime,
    [NotNull] IShellLocks locks,
    [NotNull] IPersistentIndexManager persistentIndexManager)
    : base(lifetime, NameKind.Invariant, locks, persistentIndexManager)
  {
  }


  protected override INamesProcessor TryGetProcessor(PsiLanguageType languageType)
  {
    return new NamesProcessor(NameKind.Invariant);
  }
}