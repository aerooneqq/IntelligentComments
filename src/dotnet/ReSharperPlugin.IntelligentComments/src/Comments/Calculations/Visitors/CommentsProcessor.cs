using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;
using System.Linq;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

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

  [NotNull] public static CommentProcessingResult CreateWithoutErrors([NotNull] ICommentBase comment) =>
    new(EmptyList<HighlightingInfo>.Instance, comment);
}

public interface ICommentsProcessor : IRecursiveElementProcessor
{
  IReadOnlyList<CommentProcessingResult> ProcessedComments { get; }
}

public abstract class CommentsProcessorBase : ICommentsProcessor
{
  [NotNull] [ItemNotNull] protected readonly IList<CommentProcessingResult> Comments;
  [NotNull] [ItemNotNull] protected readonly IList<ITreeNode> VisitedComments;
  protected readonly DaemonProcessKind ProcessKind;

  
  [NotNull] [ItemNotNull] public IReadOnlyList<CommentProcessingResult> ProcessedComments => Comments.AsIReadOnlyList();
  public bool ProcessingIsFinished => false;


  protected CommentsProcessorBase(DaemonProcessKind processKind)
  {
    ProcessKind = processKind;
    Comments = new List<CommentProcessingResult>();
    VisitedComments = new List<ITreeNode>();
  }

  
  public abstract void ProcessBeforeInterior(ITreeNode element);
  
  public bool InteriorShouldBeProcessed(ITreeNode element) => true;
  public void ProcessAfterInterior(ITreeNode element)
  {
  }
}