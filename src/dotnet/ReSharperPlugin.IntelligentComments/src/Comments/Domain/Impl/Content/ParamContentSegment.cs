using System.Collections.Generic;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content
{
  public class ParamContentSegment : EntityWithContentSegments, IParamContentSegment
  {
    public string Name { get; }


    public ParamContentSegment(string name) : base(Content.ContentSegments.GetEmpty())
    {
      Name = name;
    }
  }
}