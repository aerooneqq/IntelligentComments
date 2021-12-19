using JetBrains.Annotations;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

public interface ISeeAlsoContentSegment : IContentSegment
{
  [NotNull] public IHighlightedText HighlightedText { get; }
  [NotNull] public IReference Reference { get; }
}

public interface ISeeAlsoMemberContentSegment : ISeeAlsoContentSegment
{
  [NotNull] public new ICodeEntityReference Reference { get; }
}

public interface ISeeAlsoLinkContentSegment : ISeeAlsoContentSegment
{
  [NotNull] public new IExternalReference Reference { get; }
}