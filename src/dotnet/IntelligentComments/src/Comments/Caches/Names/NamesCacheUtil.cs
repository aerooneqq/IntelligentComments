using System;
using IntelligentComments.Comments.Caches.Names.Entities;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using JetBrains.ProjectModel;

namespace IntelligentComments.Comments.Caches.Names;

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