using System;
using System.Collections.Generic;
using System.Linq;
using IntelligentComments.Comments.Caches.Names;
using IntelligentComments.Comments.Caches.Names.Entities;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Settings;
using IntelligentComments.Rider.Comments.Domain;
using JetBrains.Annotations;
using JetBrains.Application.Parts;
using JetBrains.Application.Threading;
using JetBrains.Collections;
using JetBrains.Collections.Viewable;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Protocol;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using JetBrains.Rider.Model;
using JetBrains.Threading;
using JetBrains.Util;

namespace IntelligentComments.Rider.Comments.Caches.Host;

[SolutionComponent(Instantiation.DemandAnyThreadSafe)]
public class NamedEntitiesHost
{
  private readonly Lifetime myLifetime;
  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly IPsiCaches myPsiCaches;
  [NotNull] private readonly ISolution mySolution;
  [NotNull] private readonly ICommentsSettings mySettings;
  [NotNull] private readonly IPersistentIndexManager myIndex;
  [NotNull] private readonly Dictionary<NameKind, Dictionary<IPsiSourceFile, IEnumerable<RdNamedEntityItem>>> myCachedChanges;
  [NotNull] private readonly ISignal<RdFileNames> myNamedEntitiesChangeSignal;
  [NotNull] private readonly IEnumerable<INamesCache> myCaches;


  public NamedEntitiesHost(
    Lifetime lifetime,
    [NotNull] ILogger logger,
    [NotNull] IShellLocks shellLocks,
    [NotNull] IPsiCaches psiCaches,
    [NotNull] ISolution solution,
    [NotNull] IEnumerable<INamesCache> namesCaches,
    [NotNull] ICommentsSettings settings,
    [NotNull] IPersistentIndexManager index)
  {
    myLifetime = lifetime;
    myLogger = logger;
    myShellLocks = shellLocks;
    myPsiCaches = psiCaches;
    mySolution = solution;
    mySettings = settings;
    myIndex = index;
    myCachedChanges = new Dictionary<NameKind, Dictionary<IPsiSourceFile, IEnumerable<RdNamedEntityItem>>>();
    myNamedEntitiesChangeSignal = solution.GetProtocolSolution().GetRdCommentsModel().NamedEntitiesChange;

    myCaches = namesCaches.ToList();
    foreach (var cache in myCaches)
    {
      myCachedChanges[cache.NameKind] = new Dictionary<IPsiSourceFile, IEnumerable<RdNamedEntityItem>>();
    }
    
    settings.ExperimentalFeaturesEnabled.Change.Advise(lifetime, HandleEnabledFeaturesChange);
    shellLocks.TimedActions.Queue(
      lifetime, $"{GetType().Name}::ProcessingChanges", SendChangesToFrontend,
      TimeSpan.FromMilliseconds(700), TimedActionsHost.Recurrence.Recurring, Rgc.Guarded);
    
    foreach (var cache in myCaches)
    {
      cache.Change.Advise(lifetime, change => HandleCacheChange(cache, change));
    }
  }

  private void HandleEnabledFeaturesChange(bool newFeaturesEnabled)
  {
    myShellLocks.AssertMainThread();
    if (!newFeaturesEnabled)
    {
      foreach (var nameKind in myCachedChanges.Keys)
      {
        myCachedChanges[nameKind].Clear();
      }
    }
    else
    {
      myShellLocks.QueueReadLock(myLifetime, $"{GetType().Name}::", () =>
      {
        foreach (var file in myIndex.GetAllSourceFiles())
        {
          foreach (var cache in myCaches)
          {
            cache.MarkAsDirty(file);
          }
        }
        
        myPsiCaches.Update();
      });
    }
  }

  private void SendChangesToFrontend()
  {
    myShellLocks.AssertMainThread();
    if (!mySettings.ExperimentalFeaturesEnabled.Value) return;
      
    foreach (var (nameKind, filesChanges) in myCachedChanges)
    {
      foreach (var (sourceFile, changes) in filesChanges)
      {
        myNamedEntitiesChangeSignal.Fire(CreateNamesFor(sourceFile, nameKind, changes));
      }
    }

    foreach (var nameKind in myCachedChanges.Keys)
    {
      myCachedChanges[nameKind].Clear();
    }
  }

  private RdFileNames CreateNamesFor(
    [NotNull] IPsiSourceFile sourceFile, 
    NameKind nameKind,  
    [NotNull] IEnumerable<RdNamedEntityItem> changes)
  {
    var id = myIndex[sourceFile];
    var rdFileId = new RdSourceFileId(id.loqword, id.hiqword);
    var fileInfo = new RdFileInfo(rdFileId, id.GetHashCode(), sourceFile.DisplayName);
    return new RdFileNames(nameKind.ToRdNameKind(), fileInfo, changes.ToList());
  }

  private void HandleCacheChange(INamesCache cache, FileNamesChange change)
  {
    myShellLocks.AssertMainThread();
    if (!mySettings.ExperimentalFeaturesEnabled.Value) return;

    var changes = change.Entities.SelectNotNull(entity =>
    {
      var entityPresentation = entity.Name;
      var offset = entity.DocumentOffset?.Offset;
      var name = entity.Name;
            
      RdNamedEntityItem rdItem = cache switch
      {
        ToDoNamesCache => new RdTodoItem(name, entityPresentation, offset),
        HacksNamesCache => new RdHackItem(name, entityPresentation, offset),
        InvariantsNamesNamesCache => new RdInvariantItem(name, entityPresentation, offset),
        _ => null
      };

      if (rdItem is null)
      {
        myLogger.LogAssertion($"Received unsupported name's cache {cache?.GetType().Name}");
        return null;
      }

      return rdItem;
    });

    myShellLocks.QueueReadLock(myLifetime, $"{GetType().Name}::QueuingChangeSignal", () =>
    {
      if (change.SourceFile.Document.GetPsiSourceFile(mySolution) is not { } sourceFile) return;

      var properties = sourceFile.Properties;
      if (properties.IsGeneratedFile || properties.IsNonUserFile || !properties.ShouldBuildPsi) return;

      myCachedChanges[cache.NameKind][sourceFile] = changes;
    });
  }
}