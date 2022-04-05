using JetBrains.Annotations;
using JetBrains.Util;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface IExternalDomainReference : IDomainReference
{
}

public interface IHttpDomainReference : IExternalDomainReference
{
  [NotNull] string DisplayName { get; }
}

public interface IFileDomainReference : IExternalDomainReference
{
  [NotNull] FileSystemPath Path { get; }
}