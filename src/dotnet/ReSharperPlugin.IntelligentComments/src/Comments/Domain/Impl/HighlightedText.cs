using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl
{
  public class HighlightedText : IHighlightedText
  {
    public string Text { get; private set; }
    public IList<TextHighlighter> Highlighters { get; }


    public HighlightedText(string text) : this(text, EmptyList<TextHighlighter>.Enumerable)
    {
    }

    public HighlightedText(string text, IEnumerable<TextHighlighter> highlighters)
    {
      Text = text;
      Highlighters = highlighters.ToList();
    }

  
    public void Add(IHighlightedText other)
    {
      var length = Text.Length;

      var newHighlighters = other.Highlighters
        .Select(h => h with { StartOffset = h.StartOffset + length, EndOffset = h.EndOffset + length })
        .ToList();
    
      Highlighters.AddRange(newHighlighters);
      Text += other.Text;
    }
  }
}