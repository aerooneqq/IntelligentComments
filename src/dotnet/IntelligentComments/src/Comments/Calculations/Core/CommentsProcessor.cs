using System.Collections.Generic;
using System.Linq;
using IntelligentComments.Comments.Domain.Core;
using IntelligentComments.Comments.Domain.Impl;
using IntelligentComments.Comments.Domain.Impl.Content;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;

namespace IntelligentComments.Comments.Calculations.Core;

public record CommentProcessingResult(
  [NotNull] ICollection<HighlightingInfo> Errors,
  [NotNull] ICommentBase CommentBase)
{
  [NotNull] public static CommentProcessingResult CreateWithErrors(
    [NotNull] ICollection<HighlightingInfo> errors, 
    [NotNull] PsiLanguageType languageType, 
    DocumentRange commentRange)
  {
    var provider = LanguageManager.Instance.GetService<IHighlightersProvider>(languageType);

    const string InvalidCommentText = "Invalid comment: ";
    var highlighter = provider.TryGetReSharperHighlighter(DefaultLanguageAttributeIds.DOC_COMMENT, InvalidCommentText.Length);
    var text = new HighlightedText(InvalidCommentText, highlighter);

    var errorText = errors.FirstOrDefault()?.Highlighting.ToolTip ?? string.Empty;
    if (errorText.Length > 0)
    {
      highlighter = provider.GetErrorHighlighter(0, errorText.Length);
      text.Add(new HighlightedText(errorText, highlighter));
    }
    
    var textSegment = new TextContentSegment(text);
    
    return new CommentProcessingResult(errors, new InvalidComment(textSegment, commentRange));
  }

  [NotNull] public static CommentProcessingResult CreateSuccess([NotNull] ICommentBase comment) =>
    new(EmptyList<HighlightingInfo>.Instance, comment);
}

public record struct CommentsProcessorContext(
  DaemonProcessKind ProcessKind,
  IList<CommentProcessingResult> ProcessedComments,
  ISet<ITreeNode> VisitedNodes)
{
  public static CommentsProcessorContext Create(DaemonProcessKind processKind) =>
    new(processKind, new List<CommentProcessingResult>(), new HashSet<ITreeNode>());
}

public interface ICommentsProcessor : IRecursiveElementProcessor<CommentsProcessorContext>
{
}

public abstract class CommentsProcessorBase : ICommentsProcessor
{
  public bool IsProcessingFinished(CommentsProcessorContext context) => false;

  public void ProcessBeforeInterior(ITreeNode element, CommentsProcessorContext context)
  {
    if (context.VisitedNodes.Contains(element)) return;
    context.VisitedNodes.Add(element);
    
    ProcessBeforeInteriorInternal(element, context);
  }

  protected abstract void ProcessBeforeInteriorInternal(ITreeNode element, CommentsProcessorContext context);
  
  public bool InteriorShouldBeProcessed(ITreeNode element, CommentsProcessorContext context) => true;
  public void ProcessAfterInterior(ITreeNode element, CommentsProcessorContext context)
  {
  }
}