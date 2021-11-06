using System.Collections.Generic;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content
{
  public class ParamContentSegment : IParamContentSegment
  {
    public string Name { get; }
    public IContentSegments ContentSegments { get; }

    
    public ParamContentSegment(string name)
    {
      Name = name;
      ContentSegments = new ContentSegments(new List<IContentSegment>());
    }
  }
}