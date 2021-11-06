using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content
{
  public interface IParamContentSegment : IEntityWithContentSegments
  {
    [NotNull] string Name { get; }
  }
}