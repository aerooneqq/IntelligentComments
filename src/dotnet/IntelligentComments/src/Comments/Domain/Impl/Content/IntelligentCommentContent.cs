using IntelligentComments.Comments.Domain.Core.Content;
using JetBrains.Annotations;
using JetBrains.Rd.Util;

namespace IntelligentComments.Comments.Domain.Impl.Content;

public record IntelligentCommentContent([NotNull] IContentSegments ContentSegments) : IIntelligentCommentContent
{
  public void Print(PrettyPrinter printer)
  {
    printer.Println($"{nameof(IntelligentCommentContent)}:");
    ContentSegments.Print(printer);
  }
}