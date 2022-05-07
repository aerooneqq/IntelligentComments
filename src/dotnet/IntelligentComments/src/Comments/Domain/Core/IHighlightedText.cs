using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Rd.Base;

namespace IntelligentComments.Comments.Domain.Core;

public interface IHighlightedText : IPrintable
{
  [NotNull] string Text { get; }
  [NotNull] IList<TextHighlighter> Highlighters { get; }


  void Add([NotNull] IHighlightedText other);
  void SortHighlighters();
  void Normalize();
  void ReplaceHighlighters(IEnumerable<TextHighlighter> newHighlighters);
}