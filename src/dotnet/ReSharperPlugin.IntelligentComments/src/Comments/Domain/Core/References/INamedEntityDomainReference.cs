using JetBrains.Annotations;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface INamedEntityDomainReference : IDomainReference
{
  NameKind NameKind { get; }
  [NotNull] string Name { get; }
}