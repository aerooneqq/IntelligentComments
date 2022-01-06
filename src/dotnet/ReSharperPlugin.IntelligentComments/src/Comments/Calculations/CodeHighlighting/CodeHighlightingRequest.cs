using System.Collections.Generic;
using System.Text;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.Rider.Model;
using JetBrains.Util;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public abstract record CodeHighlightingRequest(
  [NotNull] PsiLanguageType Language,
  [NotNull] string Text,
  [NotNull] RdDocumentId DocumentId)
{
  [CanBeNull] private string myCachedText;

  
  public string CreateDocumentText()
  {
    if (myCachedText is { }) return myCachedText;

    return myCachedText = CreateDocumentTextInternal();
  }

  protected abstract string CreateDocumentTextInternal();
  
  public int CalculateTextHash()
  {
    myCachedText ??= CreateDocumentText();

    return Hash.Create(myCachedText).Value;
  }
}

public record CSharpCodeHighlightingRequest(
  [NotNull] PsiLanguageType Language,
  [NotNull] string Text,
  [NotNull] RdDocumentId DocumentId,
  [NotNull] IEnumerable<string> Imports,
  [NotNull] string Namespace) : CodeHighlightingRequest(Language, Text, DocumentId)
{
  protected override string CreateDocumentTextInternal()
  {
    var sb = new StringBuilder();

    sb.Append("namespace ").Append(Namespace).Append("{\n");
    
    foreach (var import in Imports)
    {
      sb.Append(import).Append("\n");
    }
    
    sb.Append("\n").Append("class ").Append("ASDASD { \n");
    sb.Append("public static void Foooooooooooo() { \n");
    
    sb.Append(Text);

    sb.Append("}}}");

    return sb.ToString();
  }
}