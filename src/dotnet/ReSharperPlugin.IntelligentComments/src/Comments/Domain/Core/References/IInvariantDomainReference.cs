using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface IInvariantDomainReference : IDomainReference
{
  [NotNull] string InvariantName { get; }
}