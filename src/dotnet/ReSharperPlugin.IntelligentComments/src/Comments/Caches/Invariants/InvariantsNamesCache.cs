using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.Collections;
using JetBrains.Diagnostics;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.Util;
using JetBrains.Util.PersistentMap;

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

  [NotNull] private readonly Dictionary<int, int> myNameHashToCount;
  
  public override string Version => "3";

  
  public InvariantsNamesCache(
    Lifetime lifetime,
    [NotNull] IShellLocks locks,
    [NotNull] IPersistentIndexManager persistentIndexManager)
    : base(lifetime, locks, persistentIndexManager, ourMarshaller)
  {
    myNameHashToCount = new Dictionary<int, int>();
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
  
  private static int CalculateHashFor(string name)
  {
    return Hash.Create(name).Value;
  }
  
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
  
  private void IncreaseOrDecreaseCounts([NotNull] Dictionary<string, int> namesCount, bool increase)
  {
    foreach (var (name, count) in namesCount)
    {
      var hash = CalculateHashFor(name);
      var adjustedCount = increase ? count : -count;
      if (!increase && !myNameHashToCount.ContainsKey(hash))
      {
        return;
      }

      if (increase && !myNameHashToCount.ContainsKey(hash))
      {
        myNameHashToCount[hash] = 0;
      }

      Assertion.Assert(myNameHashToCount.ContainsKey(hash), "myNameHashToCount.ContainsKey(hash)");
      
      myNameHashToCount[hash] += adjustedCount;
      Assertion.Assert(myNameHashToCount[hash] >= 0, "myNameHashToCount[hash] > 0");

      if (myNameHashToCount[hash] == 0)
      {
        myNameHashToCount.Remove(hash);
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
    if (myNameHashToCount.TryGetValue(CalculateHashFor(name), out var count))
    {
      return count;
    }

    return 0;
  }
}