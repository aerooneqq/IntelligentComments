using System.Collections.Generic;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content
{
  public class ContentSegment : IContentSegment
  {
  }

  public record ContentSegments(IList<IContentSegment> Segments) : IContentSegments;
}