using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors.CSharp;

/// <summary>
/// Specification:
/// 1. Multiline comments
/// 2. Single line comments
/// 3. Group of single line comments
/// 4. Documentation comments
/// 5. ReSharper disable [InspectionName]
/// 6. reference to invariant: [Invariant name]
/// </summary>
public class CSharpCommentsProcessor : CommentsProcessorBase
{
  public CSharpCommentsProcessor(DaemonProcessKind processKind) : base(processKind)
  {
  }
  
  
  public override void ProcessBeforeInterior(ITreeNode element)
  {
    if (VisitedNodes.Contains(element)) return;

    if (TryProcessDisablingComment(element) || TryProcessInlineReferenceComment(element))
    {
      return;
    }

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

  private bool TryProcessInlineReferenceComment([NotNull] ITreeNode node)
  {
    VisitedNodes.Add(node);

    var creator = LanguageManager.GetService<InlineReferenceCommentCreator>(node.Language);
    if (creator.TryCreate(node) is not { } commentProcessingResult) return false;
    
    Comments.Add(commentProcessingResult);
    return true;
  }
  
  private bool TryProcessDisablingComment([NotNull] ITreeNode node)
  {
    VisitedNodes.Add(node);

    var creator = LanguageManager.GetService<DisablingCommentCreator>(node.Language);
    if (creator.TryCreate(node) is not { } commentProcessingResult) return false;

    Comments.Add(commentProcessingResult);
    return true;
  }

  private void ProcessDocCommentBlock([NotNull] ICSharpDocCommentBlock docCommentBlock)
  {
    VisitedNodes.Add(docCommentBlock);

    if (ProcessKind is DaemonProcessKind.VISIBLE_DOCUMENT or DaemonProcessKind.SOLUTION_ANALYSIS)
    {
      var errorsCollector = new CSharpCommentProblemsCollector(docCommentBlock);
      if (errorsCollector.Run() is { Count: > 0 } errors)
      {
        var range = docCommentBlock.GetDocumentRange();
        Comments.Add(CommentProcessingResult.CreateWithErrors(errors, CSharpLanguage.Instance!, range));
        return;
      } 
    }

    if (ProcessKind is DaemonProcessKind.VISIBLE_DOCUMENT)
    {
      var builder = new CSharpDocCommentBuilder(docCommentBlock);

      if (builder.Build() is { } comment)
      {
        Comments.Add(CommentProcessingResult.CreateSuccess(comment));
      } 
    }
  }

  private void ProcessLineComment([NotNull] ICSharpCommentNode commentNode)
  {
    if (ProcessKind is not DaemonProcessKind.VISIBLE_DOCUMENT) return;
    
    var builder = new CSharpGroupOfLineCommentsBuilder(commentNode);

    VisitedNodes.Add(commentNode);

    if (builder.Build() is not var (groupOfLineComments, includedCommentsNodes)) return;
    
    Comments.Add(CommentProcessingResult.CreateSuccess(groupOfLineComments));
    VisitedNodes.AddRange(includedCommentsNodes);
  }
  
  private void ProcessMultilineComment([NotNull] ICSharpCommentNode commentNode)
  {
    if (ProcessKind is not DaemonProcessKind.VISIBLE_DOCUMENT) return;
    
    var builder = new CSharpMultilineCommentBuilder(commentNode);
    VisitedNodes.Add(commentNode);
    
    if (builder.Build() is { } multilineComment)
    {
      Comments.Add(CommentProcessingResult.CreateSuccess(multilineComment));
    }
  }
}