using System.Collections.Generic;
using System.Text;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.Rider.Model;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public abstract record CodeHighlightingRequest(
  [NotNull] PsiLanguageType Language,
  [NotNull] string Text,
  [NotNull] RdDocumentId DocumentId)
{
  public abstract string CreateDocumentText();
}

public record CSharpCodeHighlightingRequest(
  [NotNull] PsiLanguageType Language,
  [NotNull] string Text,
  [NotNull] RdDocumentId DocumentId,
  IEnumerable<string> Imports) : CodeHighlightingRequest(Language, Text, DocumentId)
{
  public override string CreateDocumentText()
  {
    var sb = new StringBuilder();
    foreach (var import in Imports)
    {
      sb.Append(import).Append("\n");
    }
    
    sb.Append(Text);

    return sb.ToString();
  }
}