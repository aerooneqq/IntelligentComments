using System.Collections.Generic;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

public class ParagraphContentSegment : EntityWithContentSegments, IParagraphContentSegment
{
  public ParagraphContentSegment(IContentSegments contentSegments) : base(contentSegments)
  {
  }
}