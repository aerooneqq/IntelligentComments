using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

public enum FontStyle
{
  Regular,
  Bold,
  Italic
}

public record TextHighlighterAttributes(FontStyle FontStyle, bool Underline, double FontWeight)
{
  [NotNull] public static TextHighlighterAttributes DefaultAttributes { get; } = new(FontStyle.Regular, false, 400);
}

public record TextHighlighter(
  string Key,
  int StartOffset,
  int EndOffset,
  TextHighlighterAttributes Attributes,
  [CanBeNull] IReadOnlyList<IReference> References = null,
  [CanBeNull] TextAnimation TextAnimation = null,
  bool IsResharperHighlighter = false)
{
  public bool IsValid() => StartOffset >= 0 && StartOffset < EndOffset;
  
  public TextHighlighter Shift(int delta)
  {
    return this with { StartOffset = StartOffset + delta, EndOffset = EndOffset + delta };
  }
}

public abstract class TextAnimation
{
}

public class UnderlineTextAnimation : TextAnimation
{
  public static UnderlineTextAnimation Instance { get; } = new UnderlineTextAnimation();
  
  
  private UnderlineTextAnimation() { }
}