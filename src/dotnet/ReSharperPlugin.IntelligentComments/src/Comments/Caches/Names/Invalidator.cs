using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.Collections;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Daemon;
using JetBrains.ReSharper.Daemon.Impl;
using JetBrains.ReSharper.Feature.Services.Text;
using JetBrains.ReSharper.Psi;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Names;

[SolutionComponent]
public class Invalidator
{
  [NotNull] private readonly SourcesTrigramIndex myTrigramIndex;
  [NotNull] private readonly DaemonImpl myDaemonImpl;
  [NotNull] private readonly SolutionAnalysisService mySolutionAnalysisService;
  [NotNull] private readonly Dictionary<IPsiSourceFile, Dictionary<NameKind, ICollection<NamedEntity>>> myEntities;

  
  public Invalidator(
    Lifetime lifetime, 
    [NotNull] [ItemNotNull] IEnumerable<INamesCache> caches, 
    [NotNull] SourcesTrigramIndex trigramIndex, 
    [NotNull] DaemonImpl daemonImpl,
    [NotNull] SolutionAnalysisService solutionAnalysisService)
  {
    myTrigramIndex = trigramIndex;
    myDaemonImpl = daemonImpl;
    mySolutionAnalysisService = solutionAnalysisService;
    myEntities = new Dictionary<IPsiSourceFile, Dictionary<NameKind, ICollection<NamedEntity>>>();
    
    foreach (var cache in caches)
    {
      cache.Change.Advise(lifetime, change =>
      {
        var sourceFile = change.SourceFile;
        if (!sourceFile.IsValid())
        {
          myEntities.Remove(sourceFile);
          return;
        }

        var entitiesByKinds = myEntities.GetOrCreateValue(sourceFile, static () => new Dictionary<NameKind, ICollection<NamedEntity>>());
        var kind = cache.NameKind;
        var newValues = new List<NamedEntity>(change.Entities.ToList());
        var oldValues = entitiesByKinds.TryGetValue(kind, out var oldValuesFromDict) switch
        {
          true => oldValuesFromDict,
          false => EmptyList<NamedEntity>.Collection.AsCollection()
        };

        entitiesByKinds[kind] = newValues;

        if (change.IsCacheLoaded)
        {
          Invalidate(oldValues, newValues, kind);
        }
      });
    }
  }

  
  private void Invalidate([NotNull] ICollection<NamedEntity> old, [NotNull] ICollection<NamedEntity> @new, NameKind kind)
  {
    var newMap = CreateNameWithKindsToCountMap(@new.Select(entity => new NameWithKind(entity.Name, kind)));
    var oldMap = CreateNameWithKindsToCountMap(old.Select(entity => new NameWithKind(entity.Name, kind)));
    var namesToInvalidate = new HashSet<NameWithKind>();

    void FillInvalidationSet(Dictionary<NameWithKind, int> firstMap, Dictionary<NameWithKind, int> secondMap)
    {
      foreach (var (nameWithKind, newCount) in firstMap)
      {
        if (secondMap.TryGetValue(nameWithKind, out var oldCount))
        {
          if (newCount != oldCount)
          {
            namesToInvalidate.Add(nameWithKind);
          }
        }
      }
    }
    
    FillInvalidationSet(newMap, oldMap);
    FillInvalidationSet(oldMap, newMap);
    
    foreach (var file in myTrigramIndex.GetFilesContainingAllWords(namesToInvalidate.Select(name => name.Name)))
    {
      myDaemonImpl.Invalidate(file.Document);
      mySolutionAnalysisService.ReanalyzeFile(file);
    }
  }
  
  [NotNull]
  private static Dictionary<NameWithKind, int> CreateNameWithKindsToCountMap(
    [NotNull] IEnumerable<NameWithKind> namesWithKinds)
  {
    var map = new Dictionary<NameWithKind, int>();
    foreach (var nameWithKind in namesWithKinds)
    {
      if (map.TryGetValue(nameWithKind, out var count))
      {
        map[nameWithKind] = ++count;
      }
      else
      {
        map[nameWithKind] = 1;
      }
    }

    return map;
  }
}