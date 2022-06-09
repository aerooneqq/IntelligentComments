using JetBrains.ReSharper.Psi;

namespace IntelligentComments.Comments.Calculations.CodeHighlighting;

/// <summary>
/// Code highlighter is an interface which is responsible for highlighting code in example section in documentation
/// comment. There are two code highlighters: <see cref="IPreliminaryCodeHighlighter"/> and <see cref="IFullCodeHighlighter"/>.
/// <see cref="IPreliminaryCodeHighlighter"/> performs first steps in highlighting code, while <see cref="IFullCodeHighlighter"/>
/// lazily and fully highlights example code fragment. Heavy operations can be moved to <see cref="IFullCodeHighlighter"/> while
/// lightweight operations (in other words initial highlighting of code examples) can be placed in <see cref="IPreliminaryCodeHighlighter"/>
/// </summary>
public interface ICodeHighlighter : IRecursiveElementProcessor<CodeHighlightingContext>
{
}