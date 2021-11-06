using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content
{
  public interface IParagraphContentSegment : IContentSegment
  {
    [NotNull] public IContentSegments ContentSegments { get; }
  }
}