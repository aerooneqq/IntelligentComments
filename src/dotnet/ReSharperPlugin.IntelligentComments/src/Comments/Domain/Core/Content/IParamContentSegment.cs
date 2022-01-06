using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

public interface IParamContentSegment : IEntityWithContentSegments
{
  [NotNull] IHighlightedText Name { get; }
}

public interface ITypeParamSegment : IParamContentSegment
{
}