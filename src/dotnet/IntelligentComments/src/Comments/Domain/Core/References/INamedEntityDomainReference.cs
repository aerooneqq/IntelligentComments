using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using JetBrains.Annotations;

namespace IntelligentComments.Comments.Domain.Core.References;

public interface INamedEntityDomainReference : IDomainReference
{
  NameKind NameKind { get; }
  [NotNull] string Name { get; }
}