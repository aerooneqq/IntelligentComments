using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

public class ParamContentSegment : EntityWithContentSegments, IParamContentSegment
{
  public IHighlightedText Name { get; }


  public ParamContentSegment(IHighlightedText name) : base(Content.ContentSegments.CreateEmpty())
  {
    Name = name;
  }
}

public class TypeParamSegment : ParamContentSegment, ITypeParamSegment
{
  public TypeParamSegment(IHighlightedText name) : base(name)
  {
  }
}