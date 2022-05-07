using IntelligentComments.Comments.Domain.Core.References;
using IntelligentComments.Comments.Domain.Impl.References;
using JetBrains.Annotations;
using JetBrains.ProjectModel;

namespace IntelligentComments.Comments.Calculations.Core.DocComments.Tickets;

[SolutionComponent]
public class GithubTicketSourceParser : ITicketSourceParser
{
  //e.g. https://github.com/dotnet/core/issues/7341
  private const string Issues = "issues";
  [NotNull] private const string Pattern = @"https:\/\/github\.com\/.*\/issues\/[0-9]+";


  public IExternalDomainReference TryParse(string sourceValue)
  {
    if (TicketSourceParserUtil.TryGetDisplayName(sourceValue, Pattern, Issues) is not { } displayName) return null;
    displayName = $"[Github]: {displayName}";
    return new HttpDomainReference(displayName, sourceValue);
  }
}