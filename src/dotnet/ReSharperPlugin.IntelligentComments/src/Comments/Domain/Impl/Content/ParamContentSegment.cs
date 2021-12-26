using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

public class ParamContentSegment : EntityWithContentSegments, IParamContentSegment
{
  public string Name { get; }


  public ParamContentSegment(string name) : base(Content.ContentSegments.CreateEmpty())
  {
    Name = name;
  }
}

public class TypeParamSegment : ParamContentSegment, ITypeParamSegment
{
  public TypeParamSegment(string name) : base(name)
  {
  }
}