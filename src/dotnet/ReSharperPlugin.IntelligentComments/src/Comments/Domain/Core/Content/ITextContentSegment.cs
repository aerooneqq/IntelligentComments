using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

public interface ITextContentSegment : IContentSegment
{
  [NotNull] IHighlightedText Text { get; }
}