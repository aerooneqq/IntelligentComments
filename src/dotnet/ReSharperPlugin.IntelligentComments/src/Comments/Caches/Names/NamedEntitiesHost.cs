using System;
using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
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
using ReSharperPlugin.IntelligentComments.Comments.Domain;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Names;

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
    var namedEntitiesChangeSignal = solution.GetProtocolSolution().GetRdCommentsModel().NamedEntitiesChange;
    
    foreach (var cache in namesCaches)
    {
      cache.Change.Advise(lifetime, change =>
      {
        var changes = change.Entities.SelectNotNull(entity =>
        {
          var entityPresentation = entity.Name;
          var offset = entity.DocumentOffset?.Offset;
          
          RdNamedEntityItem rdItem = cache switch
          {
            ToDoNamesCache => new RdTodoItem(entityPresentation, offset),
            HacksNamesCache => new RdHackItem(entityPresentation, offset),
            InvariantsNamesNamesCache => new RdInvariantItem(entityPresentation, offset),
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
          
          var id = index[sourceFile].GetHashCode();
          var fileInfo = new RdFileInfo(id, sourceFile.DisplayName);
          namedEntitiesChangeSignal.Fire(new RdFileNames(cache.NameKind.ToRdNameKind(), fileInfo, changes.ToList()));
        });
      });
    }
  }
}