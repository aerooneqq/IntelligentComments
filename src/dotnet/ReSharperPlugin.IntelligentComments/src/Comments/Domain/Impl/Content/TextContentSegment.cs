using JetBrains.Annotations;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content
{
  public class TextContentSegment : ContentSegment, ITextContentSegment
  {
    public IHighlightedText Text { get; }


    public TextContentSegment(string text) : this(new HighlightedText(text))
    {
    }
  
    public TextContentSegment([NotNull] IHighlightedText text)
    {
      Text = text;
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
}