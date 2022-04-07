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
using ReSharperPlugin.IntelligentComments.Comments.Caches.Text.Trie;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Names;

public interface INamesCache
{
  int GetNameCount([NotNull] string name);
  [NotNull] [ItemNotNull] IEnumerable<string> GetAllNamesFor([NotNull] string prefix);
}

public abstract class AbstractNamesCache : SimpleICache<Dictionary<string, int>>, INamesCache
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

  
  [NotNull] protected readonly Trie Trie;

  
  public override string Version => "4";

  
  protected AbstractNamesCache(
    Lifetime lifetime,
    IShellLocks locks,
    IPersistentIndexManager persistentIndexManager)
    : base(lifetime, locks, persistentIndexManager, ourMarshaller)
  {
    Trie = new Trie();
  }
  
  
  public override object Build(IPsiSourceFile sourceFile, bool isStartup)
  {
    var invariants = new Dictionary<string, int>();
    foreach (var file in sourceFile.GetPsiFiles<KnownLanguage>())
    {
      TryGetProcessor(file.Language)?.Process(file, invariants);
    }
    
    return invariants;
  }

  [CanBeNull] protected abstract INamesProcessor TryGetProcessor([NotNull] PsiLanguageType languageType);
  
  public override void Merge(IPsiSourceFile sourceFile, object builtPart)
  {
    if (Map.TryGetValue(sourceFile, out var oldValue) && oldValue is { })
    {
      IncreaseOrDecreaseCounts(oldValue, false);
    }

    if (builtPart is Dictionary<string, int> newValue)
    {
      IncreaseOrDecreaseCounts(newValue, true);
    }
    
    base.Merge(sourceFile, builtPart);
  }
  
  private void IncreaseOrDecreaseCounts([NotNull] Dictionary<string, int> namesCount, bool increase)
  {
    foreach (var (name, count) in namesCount)
    {
      bool containsKey = Trie.ContainsKey(name);
      if (!increase && !containsKey)
      {
        return;
      }

      if (increase && !Trie.ContainsKey(name))
      {
        Trie.CreatePathIfNeeded(name);
      }

      Assertion.Assert(Trie.ContainsKey(name), "myNameHashToCount.ContainsKey(hash)");

      var adjustedCount = increase switch
      {
        true => count,
        false => -count
      };

      Trie.ApplyDelta(name, adjustedCount); 
    }
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
  
  public override void Drop(IPsiSourceFile sourceFile)
  {
    if (Map.TryGetValue(sourceFile, out var oldValue))
    {
      IncreaseOrDecreaseCounts(oldValue, false);
    }
    
    base.Drop(sourceFile);
  }

  public int GetNameCount(string name)
  {
    return Trie.TryGet(name) ?? 0;
  }
  
  public IEnumerable<string> GetAllNamesFor(string prefix)
  {
    return prefix == string.Empty ? Trie.GetAllInvariantsNames() : Trie.GetInvariantNamesStartsWith(prefix);
  }
}