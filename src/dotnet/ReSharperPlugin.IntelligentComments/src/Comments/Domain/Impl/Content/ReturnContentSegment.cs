using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

public class ReturnContentSegment : EntityWithContentSegments, IReturnContentSegment
{
  public ReturnContentSegment(IContentSegments contentSegments) : base(contentSegments)
  {
  }
}