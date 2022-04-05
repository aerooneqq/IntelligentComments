using System;
using System.Text.RegularExpressions;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Tickets;

[SolutionComponent]
public class GithubTicketSourceParser : ITicketSourceParser
{
  private const string Issues = "issues";
  [NotNull] private const string Pattern = @"https:\/\/github\.com\/.*\/issues\/[0-9]+";

  
  [NotNull] private readonly ILogger myLogger;
  

  public GithubTicketSourceParser([NotNull] ILogger logger)
  {
    myLogger = logger;
  }
  
  
  public IExternalDomainReference TryParse(string sourceValue)
  {
    if (Regex.Matches(sourceValue, Pattern).Count != 1) return null;

    var issuesIndex = sourceValue.LastIndexOf(Issues, StringComparison.Ordinal);
    if (issuesIndex == -1)
    {
      myLogger.Error($"Somehow issues was not found after match  in {sourceValue}");
      return null;
    }

    var issueNumber = sourceValue[(issuesIndex + Issues.Length + 1)..];
    var displayName = $"Issue {issueNumber}";
    return new HttpDomainReference(displayName, sourceValue);
  }
}