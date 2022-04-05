using System;
using System.Text.RegularExpressions;
using JetBrains.ProjectModel;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Tickets;

[SolutionComponent]
public class YoutrackTicketSourceParser : ITicketSourceParser
{
  //e.g: https://youtrack.jetbrains.com/issue/RIDER-68551
  private const string Issue = "issue";
  private const string Pattern = @"https:\/\/youtrack..*\/issue\/.+";

  private readonly ILogger myLogger;


  public YoutrackTicketSourceParser(ILogger logger)
  {
    myLogger = logger;
  }
  
  
  public IExternalDomainReference TryParse(string sourceValue)
  {
    if (Regex.Matches(sourceValue, Pattern).Count != 1) return null;

    var issuesIndex = sourceValue.LastIndexOf(Issue, StringComparison.Ordinal);
    if (issuesIndex == -1)
    {
      myLogger.Error($"Somehow {Issue} was not found after match in {sourceValue}");
      return null;
    }

    var issueNumber = sourceValue[(issuesIndex + Issue.Length + 1)..];
    var displayName = $"[YT]: {issueNumber}";
    return new HttpDomainReference(displayName, sourceValue);
  }
}