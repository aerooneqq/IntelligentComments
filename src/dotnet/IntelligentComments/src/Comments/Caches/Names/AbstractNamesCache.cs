using System.Collections.Generic;
using System.Linq;
using IntelligentComments.Comments.Caches.Names.Entities;
using IntelligentComments.Comments.Caches.Text.Trie;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.Application.Progress;
using JetBrains.Application.Threading;
using JetBrains.Collections;
using JetBrains.Diagnostics;
using JetBrains.DocumentModel;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.Util.PersistentMap;

namespace IntelligentComments.Comments.Caches.Names;

public record struct NamedEntity([NotNull] string Name, DocumentOffset? DocumentOffset);

/// <summary>
/// The change of names in a single source file
/// </summary>
/// <param name="SourceFile"></param>
/// <param name="Entities"></param>
/// <param name="IsCacheLoaded">Indicates whether this change was fired after the cache was loaded or not</param>
public record struct FileNamesChange(
  [NotNull] IPsiSourceFile SourceFile, [NotNull] IEnumerable<NamedEntity> Entities, bool IsCacheLoaded);

/// <summary>
/// Despite the general cache functions, name's cache also exposes the signal to subscribe for changes of names in a file,
/// this is used to update "Hacks, Todos and Invariants" tool window content.
/// </summary>
public interface INamesCache : IPsiSourceFileCache
{
  public NameKind NameKind { get; }
  [NotNull] JetBrains.DataFlow.ISignal<FileNamesChange> Change { get; }

  int GetNameCount([NotNull] string name);
  [NotNull] [ItemNotNull] IEnumerable<string> GetAllNamesFor([NotNull] string prefix);
}

/// <summary>
/// Base class for named entities caches (there are different caches for hacks, invariants and names). The cache
/// contains the dictionary for each file with names as keys and count of name occurence in file as value. Moreover
/// the cache contains trie which allows finding all names for given prefix.
/// </summary>
/// <seealso cref="HacksNamesCache" />
/// <seealso cref="InvariantsNamesNamesCache" />
/// <seealso cref="ToDoNamesCache" />
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

  
  private bool myIsLoaded;
  [NotNull] private readonly ICommentsSettings mySettings;


  public NameKind NameKind { get; }
  [NotNull] protected Trie Trie { get; }

  
  public JetBrains.DataFlow.ISignal<FileNamesChange> Change { get; }
  public override string Version => "4";

  
  protected AbstractNamesCache(
    Lifetime lifetime,
    NameKind nameKind,
    [NotNull] IShellLocks locks,
    [NotNull] ICommentsSettings settings,
    [NotNull] IPersistentIndexManager persistentIndexManager)
    : base(lifetime, locks, persistentIndexManager, ourMarshaller)
  {
    mySettings = settings;
    NameKind = nameKind;
    Trie = new Trie();
    Change = new JetBrains.DataFlow.Signal<FileNamesChange>($"{GetType().Name}::{nameof(Change)}");
  }
  
  
  public override object Build(IPsiSourceFile sourceFile, bool isStartup)
  {
    var nameInfos = new Dictionary<string, List<NamedEntityInfo>>();
    foreach (var file in sourceFile.GetPsiFiles<KnownLanguage>())
    {
      TryGetProcessor(file.Language)?.Process(file, nameInfos);
    }

    var entities = new List<NamedEntity>();
    foreach (var (name, infos) in nameInfos)
    {
      foreach (var info in infos)
      {
        entities.Add(new NamedEntity(name, info.Offset));
      }
    }
    
    QueueChanges(sourceFile, entities);
    return nameInfos.ToDictionary(pair => pair.Key, pair => pair.Value.Count);
  }

  private void QueueChanges([NotNull] IPsiSourceFile sourceFile, [NotNull] IEnumerable<NamedEntity> entities)
  {
    if (!mySettings.ExperimentalFeaturesEnabled.Value || !sourceFile.IsValid()) return;

    Locks.QueueReadLockOrRunSync(Lifetime, $"{GetType().Name}::QueueingChange", () =>
    {
      Change.Fire(new FileNamesChange(sourceFile, entities, myIsLoaded));
    });
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
      var containsKey = Trie.ContainsKey(name);
      if (!increase && !containsKey) return;

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
    
    foreach (var (file, namesCount) in Map)
    {
      var entities = new List<NamedEntity>();
      foreach (var (name, count) in namesCount)
      {
        if (count != 1) continue;
        
        entities.Add(new NamedEntity(name, null));
      }
      
      QueueChanges(file, entities);
      IncreaseOrDecreaseCounts(namesCount, true);
    }

    myIsLoaded = true;
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