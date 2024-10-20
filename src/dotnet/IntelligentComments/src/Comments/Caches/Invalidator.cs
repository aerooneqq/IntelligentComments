using System;
using System.Collections.Generic;
using System.Linq;
using IntelligentComments.Comments.Caches.Names;
using IntelligentComments.Comments.Calculations.Core;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Daemon;
using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.Application.Parts;
using JetBrains.Collections;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Daemon;
using JetBrains.ReSharper.Daemon.Impl;
using JetBrains.ReSharper.Feature.Services.Text;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.TextControl;
using JetBrains.TextControl.DocumentMarkup;
using JetBrains.Util;

namespace IntelligentComments.Comments.Caches;

/// <summary>
/// Solution component which implements the invalidation logic: if we detected that the names in file changed, we need
/// to invalidate other files, where changed names were used and force rehighlight of opened files where changed name
/// contains. Moreover when user changes "Use experimental features setting" we need to perform invalidation.
/// </summary>
[SolutionComponent(Instantiation.DemandAnyThreadSafe)]
public class Invalidator
{
  [NotNull] private readonly SourcesTrigramIndex myTrigramIndex;
  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly IPersistentIndexManager myPersistentIndexManager;
  [NotNull] private readonly DaemonImpl myDaemonImpl;
  [NotNull] private readonly ITextControlManager myTextControlManager;
  [NotNull] private readonly IDocumentMarkupManager myDocumentMarkupManager;
  [NotNull] private readonly SolutionAnalysisService mySolutionAnalysisService;

  [NotNull] private readonly Dictionary<IPsiSourceFile, Dictionary<NameKind, ICollection<NamedEntity>>> myCurrentEntities;


  public Invalidator(
    Lifetime lifetime,
    [NotNull] ISolution solution,
    [NotNull] ILogger logger,
    [NotNull] [ItemNotNull] IEnumerable<INamesCache> caches,
    [NotNull] ICommentsSettings settings,
    [NotNull] IPersistentIndexManager persistentIndexManager,
    [NotNull] DaemonImpl daemonImpl,
    [NotNull] ITextControlManager textControlManager,
    [NotNull] IDocumentMarkupManager documentMarkupManager,
    [NotNull] SolutionAnalysisService solutionAnalysisService)
  {
    myTrigramIndex = solution.GetComponent<SourcesTrigramIndex>();
    myLogger = logger;
    myPersistentIndexManager = persistentIndexManager;
    myDaemonImpl = daemonImpl;
    myTextControlManager = textControlManager;
    myDocumentMarkupManager = documentMarkupManager;
    mySolutionAnalysisService = solutionAnalysisService;
    myCurrentEntities = new Dictionary<IPsiSourceFile, Dictionary<NameKind, ICollection<NamedEntity>>>();
    
    settings.ExperimentalFeaturesEnabled.Advise(lifetime, _ => InvalidateEverything());
    
    foreach (var cache in caches)
    {
      cache.Change.Advise(lifetime, change => HandleCacheChange(cache, change));
    }
  }

  private void HandleCacheChange([NotNull] INamesCache cache, FileNamesChange change)
  {
    var sourceFile = change.SourceFile;
    if (!sourceFile.IsValid())
    {
      myCurrentEntities.Remove(sourceFile);
      return;
    }

    var kind = cache.NameKind;
    var newValues = new List<NamedEntity>(change.Entities.ToList());
    var entitiesByKinds = myCurrentEntities.GetOrCreateValue(
      sourceFile, static () => new Dictionary<NameKind, ICollection<NamedEntity>>());
    
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
  }

  private void InvalidateEverything()
  {
    using (ReadLockCookie.Create())
    {
      InvalidateFiles(myPersistentIndexManager.GetAllSourceFiles());
    }
  }

  private void InvalidateFiles([NotNull] IEnumerable<IPsiSourceFile> files)
  {
    var openedDocuments = myTextControlManager.TextControls.Select(editor => editor.Document).ToHashSet();
    foreach (var sourceFile in files)
    {
      mySolutionAnalysisService.ReanalyzeFile(sourceFile);
      var document = sourceFile.Document;

      if (!openedDocuments.Contains(document)) continue;
      
      var model = myDocumentMarkupManager.GetMarkupModel(document);
      var commentHighlighters = model.GetHighlightersEnumerable(
        highlighterFilter: static highlighter => highlighter.UserData is CommentFoldingHighlighting);
      
      foreach (var highlighter in commentHighlighters)
      {
        model.RemoveHighlighter(highlighter);
      }
      
      myDaemonImpl.ForceReHighlight(document);
    }
  }

  private void Invalidate([NotNull] ICollection<NamedEntity> old, [NotNull] ICollection<NamedEntity> @new, NameKind kind)
  {
    var newMap = CreateNameWithKindsToCountMap(@new.Select(entity => new NameWithKind(entity.Name, kind)));
    var oldMap = CreateNameWithKindsToCountMap(old.Select(entity => new NameWithKind(entity.Name, kind)));
    var namesToInvalidate = new HashSet<NameWithKind>();

    FillInvalidationSet(newMap, oldMap, namesToInvalidate);
    FillInvalidationSet(oldMap, newMap, namesToInvalidate);

    var names = namesToInvalidate.Select(name => name.Name).ToList();

    try
    {
      if (names.Count != 0)
      {
        InvalidateFiles(myTrigramIndex.GetFilesContainingAnyWords(names));
      }
    }
    catch (Exception ex)
    {
      myLogger.Warn(ex);
    }
  }

  private void FillInvalidationSet(
    [NotNull] in Dictionary<NameWithKind, int> firstMap, 
    [NotNull] in Dictionary<NameWithKind, int> secondMap, 
    [NotNull] in HashSet<NameWithKind> namesToInvalidate)
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
      else
      {
        namesToInvalidate.Add(nameWithKind);
      }
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