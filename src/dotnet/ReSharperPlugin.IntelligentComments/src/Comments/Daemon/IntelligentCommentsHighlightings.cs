using JetBrains.ReSharper.Feature.Services.Daemon;

namespace ReSharperPlugin.IntelligentComments.Comments.Daemon;

[RegisterStaticHighlightingsGroup("Intelligent Comments", false)]
public static class IntelligentCommentsHighlightings
{
  public const string GroupId = nameof(IntelligentCommentsHighlightings);
}