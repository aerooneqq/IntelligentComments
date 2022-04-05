using JetBrains.Annotations;
using JetBrains.ProjectModel;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Tickets;

public interface ITicketSourceParser
{
  [CanBeNull] IExternalDomainReference TryParse(string sourceValue);
}

public static class TicketSourceParserUtil
{
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
}