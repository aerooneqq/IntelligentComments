using JetBrains.Annotations;
using JetBrains.Rd.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

public record IntelligentCommentContent([NotNull] IContentSegments ContentSegments) : IIntelligentCommentContent
{
  public void Print(PrettyPrinter printer)
  {
    printer.Println($"{nameof(IntelligentCommentContent)}:");
    ContentSegments.Print(printer);
  }
}