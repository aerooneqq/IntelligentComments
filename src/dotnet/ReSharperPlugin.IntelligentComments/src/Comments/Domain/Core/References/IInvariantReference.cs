using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface IInvariantReference : IReference
{
  [NotNull] string InvariantName { get; }
}