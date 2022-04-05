using JetBrains.DocumentModel;
using JetBrains.ReSharper.Feature.Services.Daemon;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Errors;

[StaticSeverityHighlighting(Severity.ERROR, typeof(CommentError), OverlapResolve = OverlapResolveKind.ERROR)]
public class CommentError : IHighlighting
{
  private readonly DocumentRange myRange;
  
  
  public string ToolTip { get; }
  public string ErrorStripeToolTip { get; }


  public CommentError(DocumentRange range, string errorMessage)
  {
    myRange = range;
    ToolTip = errorMessage;
    ErrorStripeToolTip = errorMessage;
  }

  
  public bool IsValid() => myRange.IsValid();
  public DocumentRange CalculateRange() => myRange;
}