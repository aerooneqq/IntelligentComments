using System;
using JetBrains.ProjectModel;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names.Hacks;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names.Invariants;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names.Todos;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Names;

public static class NamesCacheUtil
{
  public static INamesCache GetCacheFor(ISolution solution, NameKind nameKind) => nameKind switch
  {
    NameKind.Hack => solution.GetComponent<HacksNamesCache>(),
    NameKind.Todo => solution.GetComponent<ToDoNamesCache>(),
    NameKind.Invariant => solution.GetComponent<InvariantsNamesNamesCache>(),
    _ => throw new ArgumentOutOfRangeException(nameKind.ToString())
  };
}