using System.Collections.Generic;
using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

public interface IContentSegment
{
}

public interface IContentSegments
{
  [NotNull] IEnumerable<IContentSegment> Segments { get; }
}