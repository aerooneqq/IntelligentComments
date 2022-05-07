using JetBrains.Annotations;

namespace IntelligentComments.Comments.Domain.Core.Content;

public interface ITextContentSegment : IContentSegment
{
  [NotNull] IHighlightedText Text { get; }

  void Normalize();
}