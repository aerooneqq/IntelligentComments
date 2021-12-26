using System.Collections.Generic;
using JetBrains.Annotations;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;


public record ContentSegments(IList<IContentSegment> Segments) : IContentSegments
{
  public static ContentSegments CreateEmpty() => new(new List<IContentSegment>());
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

  
  public ExceptionContentSegment(string name) : base(Content.ContentSegments.CreateEmpty())
  {
    ExceptionName = name;
  }
}

public abstract class SeeAlsoContentSegment : ISeeAlsoContentSegment
{
  public IHighlightedText HighlightedText { get; }
  public IReference Reference { get; }


  public SeeAlsoContentSegment([NotNull] IHighlightedText highlightedText, [NotNull] IReference reference)
  {
    Reference = reference;
    HighlightedText = highlightedText;
  }
}

public class SeeAlsoMemberContentSegment : SeeAlsoContentSegment, ISeeAlsoMemberContentSegment 
{
  [NotNull] private readonly ICodeEntityReference myReference;

  
  ICodeEntityReference ISeeAlsoMemberContentSegment.Reference => myReference;
  
  
  public SeeAlsoMemberContentSegment(
    [NotNull] IHighlightedText highlightedText, 
    [NotNull] ICodeEntityReference reference) 
    : base(highlightedText, reference)
  {
    myReference = reference;
  }
}

public class SeeAlsoLinkContentSegment : SeeAlsoContentSegment, ISeeAlsoLinkContentSegment
{
  [NotNull] private readonly IExternalReference myReference;


  IExternalReference ISeeAlsoLinkContentSegment.Reference => myReference;
  
  
  public SeeAlsoLinkContentSegment(
    [NotNull] IHighlightedText highlightedText, 
    [NotNull] IExternalReference reference) 
    : base(highlightedText, reference)
  {
    myReference = reference;
  }
}