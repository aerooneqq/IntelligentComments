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

    const string invalidCommentText = "Invalid comment: ";
    var highlighter = provider.TryGetReSharperHighlighter(DefaultLanguageAttributeIds.DOC_COMMENT, invalidCommentText.Length);
    var text = new HighlightedText(invalidCommentText, highlighter);

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

public interface ICommentsProcessor : IRecursiveElementProcessor
{
  IReadOnlyList<CommentProcessingResult> ProcessedComments { get; }
}

public abstract class CommentsProcessorBase : ICommentsProcessor
{
  [NotNull] [ItemNotNull] protected readonly IList<CommentProcessingResult> Comments;
  [NotNull] [ItemNotNull] protected readonly IList<ITreeNode> VisitedNodes;
  [NotNull] protected readonly ILanguageManager LanguageManager;
  protected readonly DaemonProcessKind ProcessKind;

  
  [NotNull] [ItemNotNull] public IReadOnlyList<CommentProcessingResult> ProcessedComments => Comments.AsIReadOnlyList();
  public bool ProcessingIsFinished => false;


  protected CommentsProcessorBase(DaemonProcessKind processKind)
  {
    LanguageManager = JetBrains.ReSharper.Psi.LanguageManager.Instance;
    ProcessKind = processKind;
    Comments = new List<CommentProcessingResult>();
    VisitedNodes = new List<ITreeNode>();
  }


  public void ProcessBeforeInterior(ITreeNode element)
  {
    if (VisitedNodes.Contains(element)) return;
    VisitedNodes.Add(element);
    
    ProcessBeforeInteriorInternal(element);
  }
  
  public abstract void ProcessBeforeInteriorInternal(ITreeNode element);
  
  public bool InteriorShouldBeProcessed(ITreeNode element) => true;
  public void ProcessAfterInterior(ITreeNode element)
  {
  }
}