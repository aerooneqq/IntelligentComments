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
    if (TicketSourceParserUtil.TryGetDisplayName(sourceValue, Pattern, Issues) is not { } displayName) return null;
    displayName = $"[Github]: {displayName}";
    return new HttpDomainReference(displayName, sourceValue);
  }
}