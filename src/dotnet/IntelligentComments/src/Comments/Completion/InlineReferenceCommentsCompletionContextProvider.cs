using IntelligentComments.Comments.Caches.Names;
using IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;
using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;

namespace IntelligentComments.Comments.Completion;

[IntellisensePart]
public class InlineReferenceCommentsCompletionContextProvider : ICodeCompletionContextProvider
{
  public bool IsApplicable(CodeCompletionContext context)
  {
    return TryGetContext(context) is { };
  }

  [CanBeNull]
  private static InlineReferenceCommentCompletionContext TryGetContext([NotNull] CodeCompletionContext context)
  {
    if (!context.Solution.GetComponent<ICommentsSettings>().ExperimentalFeaturesEnabled.Value) return null;
    if (context.File.FindTokenAt(context.CaretTreeOffset) is not { } token) return null;

    var commentNode = NamesResolveUtil.TryFindAnyCommentNode(token) ??
                      NamesResolveUtil.TryFindAnyCommentNode(token.PrevSibling);
    
    if (commentNode is not { }) return null;
    if (context.LanguageManager.TryGetService<InlineReferenceCommentOperations>(commentNode.Language) is not { } operations)
      return null;

    if (operations.TryExtractCompletionInlineReferenceInfo(commentNode, context.CaretDocumentOffset) is not { } info) return null;
    
    var range = info.NameRange;
    var textualRanges = new TextLookupRanges(range, range);
    
    return new InlineReferenceCommentCompletionContext(context, textualRanges, info);
  }

  public ISpecificCodeCompletionContext GetCompletionContext(CodeCompletionContext context)
  {
    var inlineReferenceContext = TryGetContext(context);
    Assertion.AssertNotNull(inlineReferenceContext, "inlineReferenceContext != null");

    return inlineReferenceContext;
  }
}