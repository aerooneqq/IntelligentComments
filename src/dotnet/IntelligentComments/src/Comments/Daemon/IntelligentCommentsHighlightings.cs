using JetBrains.ReSharper.Feature.Services.Daemon;

namespace IntelligentComments.Comments.Daemon;

[RegisterStaticHighlightingsGroup("Intelligent Comments", false)]
public static class IntelligentCommentsHighlightings
{
  public const string GroupId = nameof(IntelligentCommentsHighlightings);
}