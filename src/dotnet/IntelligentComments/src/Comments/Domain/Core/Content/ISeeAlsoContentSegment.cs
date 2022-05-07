using IntelligentComments.Comments.Domain.Core.References;
using JetBrains.Annotations;

namespace IntelligentComments.Comments.Domain.Core.Content;

public interface ISeeAlsoContentSegment : IContentSegment
{
  [NotNull] public IHighlightedText HighlightedText { get; }
  [NotNull] public IDomainReference DomainReference { get; }
}

public interface ISeeAlsoMemberContentSegment : ISeeAlsoContentSegment
{
}

public interface ISeeAlsoLinkContentSegment : ISeeAlsoContentSegment
{
}