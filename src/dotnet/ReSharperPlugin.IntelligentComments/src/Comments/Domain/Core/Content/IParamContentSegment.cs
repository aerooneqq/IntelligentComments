using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content
{
  public interface IParamContentSegment : IContentSegment
  {
    [NotNull] string Name { get; }
    [NotNull] IContentSegments ContentSegments { get; }
  }
}