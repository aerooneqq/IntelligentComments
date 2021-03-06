using System.Collections.Generic;
using System.Drawing;
using IntelligentComments.Comments.Domain.Core.References;
using JetBrains.Annotations;
using JetBrains.Rd.Base;
using JetBrains.Rd.Util;

namespace IntelligentComments.Comments.Domain.Core;

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
  [CanBeNull] IReadOnlyList<IDomainReference> References = null,
  [CanBeNull] TextAnimation TextAnimation = null,
  bool IsResharperHighlighter = false,
  [CanBeNull] Squiggles ErrorSquiggles = null) : IPrintable
{
  public bool IsValid() => StartOffset >= 0 && StartOffset < EndOffset;
  
  public TextHighlighter Shift(int delta)
  {
    return this with { StartOffset = StartOffset + delta, EndOffset = EndOffset + delta };
  }

  public void Print(PrettyPrinter printer)
  {
    printer.Print("Highlighter: [");
    printer.Print(Key + ", ");
    printer.Print(StartOffset + ", ");
    printer.Print(EndOffset + ", ");
    printer.Print(Attributes + ", ");

    printer.Print("References: ");
    if (References is { })
    {
      foreach (var reference in References)
      {
        printer.Print($"{reference.GetType().Name}::{reference.RawValue}, ");
      }
    }
    
    printer.Print("]");
    printer.Println();
  }
}

public record Squiggles(SquigglesKind Kind, [NotNull] string ColorKey);

public enum SquigglesKind
{
  Wave,
  Dotted
}

public abstract class TextAnimation
{
}

public class UnderlineTextAnimation : TextAnimation
{
  [NotNull] public static UnderlineTextAnimation Instance { get; } = new();
  
  
  private UnderlineTextAnimation() { }
}

public class ForegroundTextAnimation : TextAnimation
{
  public Color HoveredColor { get; }
  
  public ForegroundTextAnimation(Color hoveredColor)
  {
    HoveredColor = hoveredColor;
  }
}