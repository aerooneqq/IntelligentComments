using JetBrains.Application.Threading;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Names.Hacks;

[PsiComponent]
public class HacksNamesCache : AbstractNamesCache
{
  public HacksNamesCache(
    Lifetime lifetime, 
    IShellLocks locks,
    IPersistentIndexManager persistentIndexManager) 
    : base(lifetime, NameKind.Hack, locks, persistentIndexManager)
  {
  }

  
  protected override INamesProcessor TryGetProcessor(PsiLanguageType languageType)
  {
    return new NamesProcessor(NameKind.Hack);
  }
}