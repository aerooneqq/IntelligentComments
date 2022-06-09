using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;

namespace IntelligentComments.Comments.Caches.Names.Entities;

[PsiComponent]
public class HacksNamesCache : AbstractNamesCache
{
  public HacksNamesCache(
    Lifetime lifetime, 
    [NotNull] IShellLocks locks,
    ICommentsSettings settings,
    [NotNull] IPersistentIndexManager persistentIndexManager) 
    : base(lifetime, NameKind.Hack, locks, settings, persistentIndexManager)
  {
  }

  
  protected override INamesProcessor TryGetProcessor(PsiLanguageType languageType)
  {
    return new NamesProcessor(NameKind.Hack);
  }
}