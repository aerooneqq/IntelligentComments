using JetBrains.ReSharper.Psi;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public interface ICodeHighlighter : IRecursiveElementProcessor<CodeHighlightingContext>
{
}