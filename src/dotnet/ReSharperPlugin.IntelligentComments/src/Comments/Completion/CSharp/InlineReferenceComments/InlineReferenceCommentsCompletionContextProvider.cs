using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.InlineReferenceComments;

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
    if (context.File.FindTokenAt(context.CaretTreeOffset) is not { } token) return null;

    var commentNode = NamesResolveUtil.TryFindAnyCommentNode(token) ??
                      NamesResolveUtil.TryFindAnyCommentNode(token.PrevSibling);
    
    if (commentNode is not { }) return null;
    if (context.LanguageManager.TryGetService<InlineReferenceCommentCreator>(commentNode.Language) is not { } creator)
      return null;

    if (creator.TryExtractCompletionInlineReferenceInfo(commentNode, context.CaretDocumentOffset) is not { } info) return null;
    
    var startOffset = info.InvariantNameOffset.Offset;
    var endOffset = startOffset + info.Name.Length;
    var range = new DocumentRange(context.Document, new TextRange(startOffset, endOffset));
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