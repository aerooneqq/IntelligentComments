using JetBrains.ReSharper.Psi;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public interface IPreliminaryCodeHighlighter : IRecursiveElementProcessor
{
  public IHighlightedText Text { get; }
}