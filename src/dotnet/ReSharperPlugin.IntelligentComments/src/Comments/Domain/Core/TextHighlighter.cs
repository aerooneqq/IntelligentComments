using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

public enum FontStyle
{
  Regular,
  Bold
}

public record TextHighlighterAttributes(FontStyle FontStyle, bool Underline, double FontWeight)
{
  [NotNull] public static TextHighlighterAttributes DefaultAttributes = new(FontStyle.Regular, false, 400);
}

public record TextHighlighter(
  string Key,
  int StartOffset,
  int EndOffset,
  TextHighlighterAttributes Attributes,
  [CanBeNull] TextAnimation TextAnimation = null,
  bool IsResharperHighlighter = false)
{
  public bool IsValid() => StartOffset >= 0 && StartOffset < EndOffset;
}

public abstract class TextAnimation
{
}

public class UnderlineTextAnimation : TextAnimation
{
}