using System;
using JetBrains.Annotations;
using JetBrains.Util;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface IExternalReference : IReference
{
}

public interface IHttpReference : IExternalReference
{
}

public interface IFileReference : IExternalReference
{
  [NotNull] FileSystemPath Path { get; }
}