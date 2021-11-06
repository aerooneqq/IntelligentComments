using JetBrains.Annotations;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content
{
  public record IntelligentCommentContent([NotNull] IContentSegments ContentSegments) : IIntelligentCommentContent;
}