using System.Collections.Generic;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

public class ContentSegment : IContentSegment
{
}

public record ContentSegments(IList<IContentSegment> Segments) : IContentSegments
{
  public static ContentSegments GetEmpty() => new ContentSegments(new List<IContentSegment>());
}
  
public class EntityWithContentSegments : IEntityWithContentSegments
{
  public IContentSegments ContentSegments { get; }


  public EntityWithContentSegments(IContentSegments contentSegments)
  {
    ContentSegments = contentSegments;
  }
}

public class RemarksContentSegment : EntityWithContentSegments, IRemarksSegment
{
  public RemarksContentSegment(IContentSegments contentSegments) : base(contentSegments)
  {
  }
}

public class ExceptionContentSegment : EntityWithContentSegments, IExceptionSegment
{
  public string ExceptionName { get; }

    
  public ExceptionContentSegment(string name) : base(Content.ContentSegments.GetEmpty())
  {
    ExceptionName = name;
  }
}