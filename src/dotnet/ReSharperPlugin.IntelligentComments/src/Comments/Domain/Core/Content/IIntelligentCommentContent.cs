using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

public interface IIntelligentCommentContent
{
  [NotNull] IContentSegments ContentSegments { get; }
}