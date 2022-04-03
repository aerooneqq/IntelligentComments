using System;
using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

internal record struct ContentSegmentsMetadata(
  [CanBeNull] IEntityWithContentSegments CorrespondingEntity,
  [NotNull] IContentSegments ContentSegments)
{
  public static ContentSegmentsMetadata CreateEmpty() => new(null, Domain.Impl.Content.ContentSegments.CreateEmpty());
}

internal readonly struct WithPushedToStackContentSegments : IDisposable
{
  [NotNull] private readonly Stack<ContentSegmentsMetadata> myStack;
  [NotNull] private readonly ILogger myLogger;

    
  public WithPushedToStackContentSegments([NotNull] Stack<ContentSegmentsMetadata> stack, [NotNull] ILogger logger)
    : this(stack, ContentSegmentsMetadata.CreateEmpty(), logger)
  {
  }
      
  public WithPushedToStackContentSegments(
    [NotNull] Stack<ContentSegmentsMetadata> stack, ContentSegmentsMetadata metadata, ILogger logger)
  {
    myStack = stack;
    myLogger = logger;
    myStack.Push(metadata);
  }
    
      
  public void Dispose()
  {
    if (myStack.Count == 0)
    {
      myLogger.LogAssertion("Stack was empty before possible Pop()");
      return;
    }
        
    var contentSegments = myStack.Pop();
    var segments = contentSegments.ContentSegments.Segments;

    void Normalize()
    {
      foreach (var segment in segments)
      {
        if (segment is ITextContentSegment textContentSegment)
        {
          textContentSegment.Normalize();
        }
      }
    }
        
    var index = 0;
    while (index != segments.Count)
    {
      if (index + 1 >= segments.Count)
      {
        Normalize();
        return;
      }

      var currentSegment = segments[index];
      var nextSegment = segments[index + 1];
      if (currentSegment is not IMergeableContentSegment currentTextSegment ||
          nextSegment is not IMergeableContentSegment nextTextSegment)
      {
        ++index;
        continue;
      }
          
      currentTextSegment.MergeWith(nextTextSegment);
      segments.RemoveAt(index + 1);
    }
        
    Normalize();
  }
}