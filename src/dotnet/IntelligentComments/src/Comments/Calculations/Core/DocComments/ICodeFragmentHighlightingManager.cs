using IntelligentComments.Comments.Calculations.CodeHighlighting;
using JetBrains.Annotations;

namespace IntelligentComments.Comments.Calculations.Core.DocComments;

public interface ICodeFragmentHighlightingManager
{
  int AddRequestForHighlighting([NotNull] CodeHighlightingRequest request);
}