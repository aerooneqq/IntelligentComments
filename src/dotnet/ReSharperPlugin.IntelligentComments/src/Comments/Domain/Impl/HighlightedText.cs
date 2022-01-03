using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using JetBrains.Annotations;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

public class HighlightedText : IHighlightedText
{
  [NotNull] public static HighlightedText EmptyText { get; } = new HighlightedText(string.Empty);
  
  [NotNull] private static readonly HashSet<char> ourWhitespaceTokens = new() { ' ', '\n' };

    
  [NotNull] private List<TextHighlighter> myHighlighters;
    
    
  public string Text { get; private set; }
  public IList<TextHighlighter> Highlighters => myHighlighters;


  public HighlightedText(string text) : this(text, EmptyList<TextHighlighter>.Enumerable)
  {
  }

  public HighlightedText(string text, IEnumerable<TextHighlighter> highlighters)
  {
    Text = text;
    myHighlighters = highlighters.ToList();
  }

  public HighlightedText(string text, TextHighlighter highlighter) : this(text, new[] { highlighter })
  {
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

  public void SortHighlighters() => myHighlighters.Sort((first, second) => first.StartOffset - second.StartOffset);
    
  public void Normalize()
  {
    int removedCharsFromStartCount = 0;
    var sb = new StringBuilder(Text);
    while (sb.Length > 0 && ourWhitespaceTokens.Contains(sb[0]))
    {
      sb.Remove(0, 1);
      ++removedCharsFromStartCount;
    }

    var newHighlighters = myHighlighters
      .Select(h => h with { StartOffset = h.StartOffset - removedCharsFromStartCount })
      .Where(h => h.IsValid())
      .ToList();

    while (sb.Length > 0 && ourWhitespaceTokens.Contains(sb[^1]))
    {
      sb.Remove(sb.Length - 1, 1);
    }

    newHighlighters = newHighlighters
      .Select(h => h with { EndOffset = Math.Min(h.EndOffset, sb.Length) })
      .Where(h => h.IsValid() && h.EndOffset <= sb.Length)
      .ToList();

    Text = sb.ToString();
    myHighlighters = newHighlighters;
  }
}