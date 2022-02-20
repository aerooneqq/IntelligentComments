using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;
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

public class CommentsProcessor : IRecursiveElementProcessor
{
  [NotNull] [ItemNotNull] private readonly IList<CommentProcessingResult> myComments;
  [NotNull] [ItemNotNull] private readonly IList<ITreeNode> myVisitedComments;

  [NotNull] [ItemNotNull] public IReadOnlyList<CommentProcessingResult> Comments => myComments.AsIReadOnlyList();
  public bool ProcessingIsFinished => false;


  public CommentsProcessor()
  {
    myComments = new List<CommentProcessingResult>();
    myVisitedComments = new List<ITreeNode>();
  }


  public bool InteriorShouldBeProcessed(ITreeNode element) => true;

  public void ProcessBeforeInterior(ITreeNode element)
  {
    if (myVisitedComments.Contains(element)) return;

    switch (element)
    {
      case ICSharpDocCommentBlock docCommentBlock:
      {
        ProcessDocCommentBlock(docCommentBlock);
        break;
      }
      case ICSharpCommentNode { CommentType: CommentType.END_OF_LINE_COMMENT } commentNode:
      {
        ProcessLineComment(commentNode);
        break;
      }
      case ICSharpCommentNode { CommentType: CommentType.MULTILINE_COMMENT } commentNode:
      {
        ProcessMultilineComment(commentNode);
        break;
      }
    }
  }

  private void ProcessDocCommentBlock([NotNull] IDocCommentBlock docCommentBlock)
  {
    myVisitedComments.Add(docCommentBlock);
    
    var errorsCollector = new CommentProblemsCollector(docCommentBlock);
    if (errorsCollector.Run() is { Count: > 0 } errors)
    {
      myComments.Add(CommentProcessingResult.CreateWithErrors(errors, docCommentBlock.Language, docCommentBlock.GetDocumentRange()));
      return;
    }
    
    var builder = new DocCommentBuilder(docCommentBlock);

    if (builder.Build() is { } comment)
    {
      myComments.Add(CommentProcessingResult.CreateWithoutErrors(comment));
    }
  }

  private void ProcessLineComment([NotNull] ICSharpCommentNode commentNode)
  {
    var builder = new GroupOfLineCommentsBuilder(commentNode);

    myVisitedComments.Add(commentNode);

    if (builder.Build() is not var (groupOfLineComments, includedCommentsNodes)) return;
    
    myComments.Add(CommentProcessingResult.CreateWithoutErrors(groupOfLineComments));
    myVisitedComments.AddRange(includedCommentsNodes);
  }

  private void ProcessMultilineComment([NotNull] ICSharpCommentNode commentNode)
  {
    var builder = new MultilineCommentsBuilder(commentNode);
    
    myVisitedComments.Add(commentNode);
    
    if (builder.Build() is { } multilineComment)
    {
      myComments.Add(CommentProcessingResult.CreateWithoutErrors(multilineComment));
    }
  }

  public void ProcessAfterInterior(ITreeNode element)
  {
  }
}