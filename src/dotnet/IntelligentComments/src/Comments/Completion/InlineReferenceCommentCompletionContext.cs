using IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;

namespace IntelligentComments.Comments.Completion;

public class InlineReferenceCommentCompletionContext : SpecificCodeCompletionContext
{
  public TextLookupRanges Ranges { get; }
  public InlineReferenceCommentInfo Info { get; }
  public override string ContextId => nameof(InlineReferenceCommentCompletionContext);

  
  public InlineReferenceCommentCompletionContext(
    [NotNull] CodeCompletionContext context,
    TextLookupRanges ranges,
    InlineReferenceCommentInfo info) : base(context)
  {
    Ranges = ranges;
    Info = info;
  }
}