using System.Collections.Generic;
using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

public interface IContentSegment
{
}

public interface IContentSegments
{
  [NotNull] IList<IContentSegment> Segments { get; }
}

public interface IEntityWithContentSegments : IContentSegment
{
  [NotNull] public IContentSegments ContentSegments { get; }
}

public interface IRemarksSegment : IEntityWithContentSegments
{
}

public interface IExceptionSegment : IEntityWithContentSegments
{
  string ExceptionName { get; }
}

public interface IExampleSegment : IEntityWithContentSegments
{
}

public interface IListSegment : IContentSegment
{
  [NotNull] IList<IListItem> Items { get; }
}

public interface IListItem
{
  IEntityWithContentSegments Header { get; }
  IEntityWithContentSegments Content { get; }
}