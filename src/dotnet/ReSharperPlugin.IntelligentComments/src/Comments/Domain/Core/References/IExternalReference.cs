using System;
using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface IExternalReference : IReference
{
}

public interface IHttpReference : IExternalReference
{
  [NotNull] public string RawLink { get; }
}