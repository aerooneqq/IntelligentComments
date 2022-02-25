using System.Collections.Generic;
using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

public interface IHighlightedText
{
  [NotNull] string Text { get; }
  [NotNull] IList<TextHighlighter> Highlighters { get; }


  void Add([NotNull] IHighlightedText other);
  void SortHighlighters();
  void Normalize();
}