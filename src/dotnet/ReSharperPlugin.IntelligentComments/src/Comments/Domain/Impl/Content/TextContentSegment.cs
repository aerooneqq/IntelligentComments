using JetBrains.Annotations;
using JetBrains.Diagnostics;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

public class TextContentSegment : ITextContentSegment
{
  public IHighlightedText Text { get; }


  public TextContentSegment(string text) : this(new HighlightedText(text))
  {
  }
  
  public TextContentSegment([NotNull] IHighlightedText text)
  {
    Text = text;
  }
    
    
  public void Normalize()
  {
    Text.SortHighlighters();
    AssertOverlappingHighlighters();
    Text.Normalize();
  }
    
  private void AssertOverlappingHighlighters()
  {
    int lastRightRange = -1;
    foreach (var highlighter in Text.Highlighters)
    {
      Assertion.Assert(highlighter.StartOffset >= lastRightRange, "highlighter.StartOffset > lastRightRange");
      lastRightRange = highlighter.EndOffset;
    }
  }
}
  
public class MergeableTextContentSegment : TextContentSegment, IMergeableContentSegment
{
  public MergeableTextContentSegment(string text) : base(text)
  {
  }

  public MergeableTextContentSegment([NotNull] IHighlightedText text) : base(text)
  {
  }
    
    
  public void MergeWith(IMergeableContentSegment other)
  {
    if (other is not ITextContentSegment textContentSegment) return;
      
    Text.Add(textContentSegment.Text);
  }
}