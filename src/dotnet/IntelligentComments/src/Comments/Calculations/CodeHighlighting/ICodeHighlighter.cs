using JetBrains.ReSharper.Psi;

namespace IntelligentComments.Comments.Calculations.CodeHighlighting;

public interface ICodeHighlighter : IRecursiveElementProcessor<CodeHighlightingContext>
{
}