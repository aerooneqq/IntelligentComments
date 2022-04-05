using System;
using System.Text.RegularExpressions;
using JetBrains.ProjectModel;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Tickets;

[SolutionComponent]
public class YoutrackTicketSourceParser : ITicketSourceParser
{
  //e.g: https://youtrack.jetbrains.com/issue/RIDER-68551
  private const string Issue = "issue";
  private const string Pattern = @"https:\/\/youtrack..*\/issue\/.+";


  public IExternalDomainReference TryParse(string sourceValue)
  {
    if (TicketSourceParserUtil.TryGetDisplayName(sourceValue, Pattern, Issue) is not { } displayName) return null;
    displayName = $"[YT]: {displayName}";
    return new HttpDomainReference(displayName, sourceValue);
  }
}