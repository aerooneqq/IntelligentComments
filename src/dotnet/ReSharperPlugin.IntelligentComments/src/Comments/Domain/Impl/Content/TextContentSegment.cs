using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.Rd.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

public class TextContentSegment : ITextContentSegment
{
  public IHighlightedText Text { get; }


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
    var lastRightRange = -1;
    foreach (var highlighter in Text.Highlighters)
    {
      Assertion.Assert(highlighter.StartOffset >= lastRightRange, "highlighter.StartOffset > lastRightRange");
      lastRightRange = highlighter.EndOffset;
    }
  }

  public void Print(PrettyPrinter printer)
  {
    printer.Println($"{nameof(TextContentSegment)}:");
    Text.Print(printer);
  }
}
  
public class MergeableTextContentSegment : TextContentSegment, IMergeableContentSegment
{
  public MergeableTextContentSegment([NotNull] IHighlightedText text) : base(text)
  {
  }
    
    
  public void MergeWith(IMergeableContentSegment other)
  {
    if (other is not ITextContentSegment textContentSegment) return;
      
    Text.Add(textContentSegment.Text);
  }
}