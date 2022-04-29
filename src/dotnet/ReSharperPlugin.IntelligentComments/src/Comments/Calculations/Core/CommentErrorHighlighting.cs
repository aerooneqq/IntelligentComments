using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Feature.Services.Daemon;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Errors;

[StaticSeverityHighlighting(Severity.ERROR, typeof(CommentErrorHighlighting), OverlapResolve = OverlapResolveKind.UNRESOLVED_ERROR)]
public class CommentErrorHighlighting : IHighlighting
{
  [NotNull] public const string ErrorPrefix = "[IC]: ";
  
  [NotNull]
  public static HighlightingInfo CreateInfo([NotNull] string message, DocumentRange range)
  {
    return new HighlightingInfo(range, Create(message, range));
  }

  [NotNull]
  public static CommentErrorHighlighting Create([NotNull] string message, DocumentRange range)
  {
    Assertion.Assert(range.IsValid(), "range.IsValid()");
    var adjustedMessage = $"[IC]: {message}";
    return new CommentErrorHighlighting(range, adjustedMessage);
  }
  
  
  private readonly DocumentRange myRange;
  
  
  public string ToolTip { get; }
  public string ErrorStripeToolTip { get; }


  public CommentErrorHighlighting(DocumentRange range, string errorMessage)
  {
    myRange = range;
    ToolTip = errorMessage;
    ErrorStripeToolTip = errorMessage;
  }

  
  public bool IsValid() => myRange.IsValid();
  public DocumentRange CalculateRange() => myRange;
}