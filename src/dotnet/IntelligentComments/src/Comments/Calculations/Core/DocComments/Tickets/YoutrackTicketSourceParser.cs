using IntelligentComments.Comments.Domain.Core.References;
using IntelligentComments.Comments.Domain.Impl.References;
using JetBrains.Application.Parts;
using JetBrains.ProjectModel;

namespace IntelligentComments.Comments.Calculations.Core.DocComments.Tickets;

[SolutionComponent(Instantiation.DemandAnyThreadSafe)]
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