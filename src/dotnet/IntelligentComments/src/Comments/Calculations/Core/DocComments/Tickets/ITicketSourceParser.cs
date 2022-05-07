using System;
using System.Text.RegularExpressions;
using IntelligentComments.Comments.Domain.Core.References;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.Util;
using JetBrains.Util.Logging;

namespace IntelligentComments.Comments.Calculations.Core.DocComments.Tickets;

public interface ITicketSourceParser
{
  [CanBeNull] IExternalDomainReference TryParse(string sourceValue);
}

public static class TicketSourceParserUtil
{
  [NotNull] private static readonly ILogger ourLogger = Logger.GetLogger(nameof(TicketSourceParserUtil));
  
  
  [CanBeNull]
  public static IExternalDomainReference TryParse([NotNull] ISolution solution, [CanBeNull] string sourceValue)
  {
    if (sourceValue is null) return null;
    
    var parsers = solution.GetComponents<ITicketSourceParser>();
    foreach (var parser in parsers)
    {
      if (parser.TryParse(sourceValue) is { } externalDomainReference)
      {
        return externalDomainReference;
      }
    }

    return null;
  }

  [CanBeNull]
  internal static string TryGetDisplayName(
    [NotNull] string sourceValue, 
    [NotNull] string pattern,
    [NotNull] string lastIssuesName)
  {
    if (Regex.Matches(sourceValue, pattern).Count != 1) return null;

    var issuesIndex = sourceValue.LastIndexOf(lastIssuesName, StringComparison.Ordinal);
    if (issuesIndex == -1)
    {
      ourLogger.Error($"Somehow issues was not found after match in {sourceValue}");
      return null;
    }

    return sourceValue[(issuesIndex + lastIssuesName.Length + 1)..];
  }
}