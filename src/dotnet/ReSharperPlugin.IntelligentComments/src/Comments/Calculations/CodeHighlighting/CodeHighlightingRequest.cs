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
  [NotNull] IEnumerable<string> Imports,
  [NotNull] string Namespace) : CodeHighlightingRequest(Language, Text, DocumentId)
{
  public override string CreateDocumentText()
  {
    var sb = new StringBuilder();
    foreach (var import in Imports)
    {
      sb.Append(import).Append("\n");
    }

    sb.Append("namespace ").Append(Namespace).Append("{ class ").Append("ASDASD { \n");
    sb.Append("public static void Foooooooooooo() { \n");
    
    sb.Append(Text);

    sb.Append("}}}");

    return sb.ToString();
  }
}