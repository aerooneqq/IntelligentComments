using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Application.Progress;
using JetBrains.Application.Threading;
using JetBrains.Collections;
using JetBrains.Diagnostics;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.Util.PersistentMap;
using System.Linq;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;

[PsiComponent]
public class InvariantsNamesCache : SimpleICache<Dictionary<string, int>>
{
  [NotNull] private static readonly IUnsafeMarshaller<Dictionary<string, int>> ourMarshaller =
    UnsafeMarshallers.GetCollectionMarshaller(
      reader => new KeyValuePair<string, int>(reader.ReadString(), reader.ReadInt()),
      (writer, value) =>
      {
        writer.Write(value.Key);
        writer.Write(value.Value);
      },
      collection => new Dictionary<string, int>(collection)
    );

  [NotNull] private readonly Dictionary<string, int> myNamesToCount;
  
  public override string Version => "3";
  

  public InvariantsNamesCache(
    Lifetime lifetime,
    [NotNull] IShellLocks locks,
    [NotNull] IPersistentIndexManager persistentIndexManager)
    : base(lifetime, locks, persistentIndexManager, ourMarshaller)
  {
    myNamesToCount = new Dictionary<string, int>();
  }
  

  public override object Build(IPsiSourceFile sourceFile, bool isStartup)
  {
    var invariants = new Dictionary<string, int>();
    foreach (var file in sourceFile.GetPsiFiles<KnownLanguage>())
    {
      GetProcessor(file.Language)?.Process(file, invariants);
    }
    
    return invariants;
  }
  
  [CanBeNull]
  private static IInvariantsProcessor GetProcessor([NotNull] PsiLanguageType languageType) =>
    LanguageManager.Instance.TryGetService<IInvariantsProcessor>(languageType);
  
  public override void Merge(IPsiSourceFile sourceFile, object builtPart)
  {
    if (Map.TryGetValue(sourceFile, out var oldValue) && oldValue is { })
    {
      IncreaseOrDecreaseCounts(oldValue, false);
    }

    if ((Dictionary<string, int>)builtPart is { } newValue)
    {
      IncreaseOrDecreaseCounts(newValue, true);
    }
    
    base.Merge(sourceFile, builtPart);
  }

  public override object Load(IProgressIndicator progress, bool enablePersistence)
  {
    var obj = base.Load(progress, enablePersistence);
    
    foreach (var (_, namesCount) in Map)
    {
      IncreaseOrDecreaseCounts(namesCount, true);
    }

    return obj;
  }

  private void IncreaseOrDecreaseCounts([NotNull] Dictionary<string, int> namesCount, bool increase)
  {
    foreach (var (name, count) in namesCount)
    {
      if (!increase && !myNamesToCount.ContainsKey(name))
      {
        return;
      }

      if (increase && !myNamesToCount.ContainsKey(name))
      {
        myNamesToCount[name] = 0;
      }

      Assertion.Assert(myNamesToCount.ContainsKey(name), "myNameHashToCount.ContainsKey(hash)");
      
      var adjustedCount = increase ? count : -count;
      myNamesToCount[name] += adjustedCount;
      Assertion.Assert(myNamesToCount[name] >= 0, "myNameHashToCount[hash] > 0");

      if (myNamesToCount[name] == 0)
      {
        myNamesToCount.Remove(name);
      }
    }
  }

  public override void Drop(IPsiSourceFile sourceFile)
  {
    var oldValue = Map[sourceFile];
    IncreaseOrDecreaseCounts(oldValue, false);
    
    base.Drop(sourceFile);
  }

  public int GetInvariantNameCount([NotNull] string name)
  {
    if (myNamesToCount.TryGetValue(name, out var count))
    {
      return count;
    }

    return 0;
  }
  
  public IEnumerable<string> GetAllInvariantsNames()
  {
    return myNamesToCount.Keys.ToList();
  }
}