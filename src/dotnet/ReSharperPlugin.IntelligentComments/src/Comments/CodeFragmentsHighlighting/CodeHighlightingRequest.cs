using System.Collections.Generic;
using System.Text;
using JetBrains.Annotations;
using JetBrains.Rider.Model;

namespace ReSharperPlugin.IntelligentComments.Comments.CodeFragmentsHighlighting;

public record CodeHighlightingRequest(
  [NotNull] string Text,
  [NotNull] RdDocumentId DocumentId,
  [NotNull] IEnumerable<string> Imports)
{
  public string CreateDocumentText()
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