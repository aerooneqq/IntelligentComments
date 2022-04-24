using System;
using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.Collections;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using JetBrains.Rider.Model;
using JetBrains.Threading;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names.Hacks;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names.Invariants;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names.Todos;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Names.Host;

[SolutionComponent]
public class NamedEntitiesHost
{
  public NamedEntitiesHost(
    Lifetime lifetime, 
    [NotNull] ILogger logger,
    [NotNull] IShellLocks shellLocks,
    [NotNull] ISolution solution,
    [NotNull] IEnumerable<INamesCache> namesCaches,
    [NotNull] IPersistentIndexManager index)
  {
    var caches = namesCaches.ToList();
    var cachedChanges = new Dictionary<NameKind, Dictionary<IPsiSourceFile, IEnumerable<RdNamedEntityItem>>>();
    
    foreach (var cache in caches)
    {
      cachedChanges[cache.NameKind] = new Dictionary<IPsiSourceFile, IEnumerable<RdNamedEntityItem>>();
    }
    
    var namedEntitiesChangeSignal = solution.GetProtocolSolution().GetRdCommentsModel().NamedEntitiesChange;
    shellLocks.TimedActions.Queue(lifetime, $"{GetType().Name}::ProcessingChanges", () =>
    {
      shellLocks.AssertMainThread();
      foreach (var (nameKind, filesChanges) in cachedChanges)
      {
        if (filesChanges.Count == 0) continue;
        
        foreach (var (sourceFile, changes) in filesChanges)
        {
          var id = index[sourceFile];
          var rdFileId = new RdSourceFileId(id.loqword, id.hiqword);
          var fileInfo = new RdFileInfo(rdFileId, id.GetHashCode(), sourceFile.DisplayName);
          namedEntitiesChangeSignal.Fire(new RdFileNames(nameKind.ToRdNameKind(), fileInfo, changes.ToList()));
        }
      }

      foreach (var nameKind in cachedChanges.Keys)
      {
        cachedChanges[nameKind].Clear();
      }
    }, TimeSpan.FromMilliseconds(700), TimedActionsHost.Recurrence.Recurring, Rgc.Guarded);
    
    foreach (var cache in caches)
    {
      cache.Change.Advise(lifetime, change =>
      {
        shellLocks.AssertMainThread();
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
            logger.LogAssertion($"Received unsupported name's cache {cache?.GetType().Name}");
            return null;
          }

          return rdItem;
        });

        shellLocks.QueueReadLock(lifetime, $"{GetType().Name}::QueuingChangeSignal", () =>
        {
          if (change.SourceFile.Document.GetPsiSourceFile(solution) is not { } sourceFile) return;

          var properties = sourceFile.Properties;
          if (properties.IsGeneratedFile || properties.IsNonUserFile || !properties.ShouldBuildPsi) return;

          cachedChanges[cache.NameKind][sourceFile] = changes;
        });
      });
    }
  }
}