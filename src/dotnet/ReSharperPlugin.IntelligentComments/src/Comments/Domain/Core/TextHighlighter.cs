using JetBrains.TextControl.DocumentMarkup;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

public record TextHighlighter(
  string Key,
  int StartOffset,
  int EndOffset,
  HighlighterAttributes Attributes);