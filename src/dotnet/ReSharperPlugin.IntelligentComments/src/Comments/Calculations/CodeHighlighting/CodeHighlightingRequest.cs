using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.Util;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public record CodeHighlightingRequest(
  [NotNull] PsiLanguageType Language,
  [NotNull] string Text,
  [NotNull] IDocument Document,
  [NotNull] ISandBoxTreeNodeOperations Operations)
{
  public int CalculateTextHash()
  {
    return Hash.Create(Text).Value;
  }
}