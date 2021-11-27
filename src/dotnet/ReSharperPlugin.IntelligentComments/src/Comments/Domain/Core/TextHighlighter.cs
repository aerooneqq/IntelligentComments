using JetBrains.TextControl.DocumentMarkup;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

public enum FontStyle
{
  Regular,
  Bold
}

public record TextHighlighterAttributes(FontStyle FontStyle, bool Underline, double FontWeight);

public record TextHighlighter(
  string Key,
  int StartOffset,
  int EndOffset,
  TextHighlighterAttributes Attributes)
{
  public bool IsValid() => StartOffset >= 0 && StartOffset < EndOffset;
}