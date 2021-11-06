using System.Collections.Generic;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content
{
  public class ParagraphContentSegment : IParagraphContentSegment
  {
    public IContentSegments ContentSegments { get; }


    public ParagraphContentSegment(IContentSegments contentSegments)
    {
      ContentSegments = contentSegments;
    }
  }
}