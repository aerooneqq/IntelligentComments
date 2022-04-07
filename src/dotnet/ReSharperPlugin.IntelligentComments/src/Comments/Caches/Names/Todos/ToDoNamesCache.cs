using JetBrains.Application.Threading;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Names.Todos;

[PsiComponent]
public class ToDoNamesCache : AbstractNamesCache
{
  public ToDoNamesCache(
    Lifetime lifetime, 
    IShellLocks locks, 
    IPersistentIndexManager persistentIndexManager) 
    : base(lifetime, locks, persistentIndexManager)
  {
  }

  protected override INamesProcessor TryGetProcessor(PsiLanguageType languageType)
  {
    return new NamesProcessor(NameKind.Todo);
  }
}