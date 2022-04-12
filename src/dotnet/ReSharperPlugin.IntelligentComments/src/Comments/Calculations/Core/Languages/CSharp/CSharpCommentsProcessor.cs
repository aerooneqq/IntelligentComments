using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DisablingComments;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Errors;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments.HackComments;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments.ToDoComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.Languages.CSharp;

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
  
  
  public override void ProcessBeforeInteriorInternal(ITreeNode element)
  {
    if (TryProcessSpecificComments(element)) return;

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

  private bool TryProcessSpecificComments([NotNull] ITreeNode element)
  {
    var creators = LanguageManager.TryGetCachedServices<ICommentFromNodeCreator>(element.Language).OrderByDescending(creator => creator.Priority);
    foreach (var creator in creators)
    {
      if (creator.TryCreate(element) is var (comment, nodes))
      {
        VisitedNodes.AddRange(nodes);
        Comments.Add(CommentProcessingResult.CreateSuccess(comment));
        return true;
      }
    }

    return TryProcessDisablingComment(element);
  }
  
  private bool TryProcessDisablingComment([NotNull] ITreeNode node)
  {
    if (LanguageManager.TryGetService<DisablingCommentCreator>(node.Language) is not { } creator) return false;
    if (creator.TryCreate(node) is not { } commentProcessingResult) return false;

    Comments.Add(commentProcessingResult);
    return true;
  }

  private void ProcessDocCommentBlock([NotNull] ICSharpDocCommentBlock docCommentBlock)
  {
    if (ProcessKind is DaemonProcessKind.VISIBLE_DOCUMENT or DaemonProcessKind.SOLUTION_ANALYSIS)
    {
      var errorsCollector = LanguageManager.GetService<ICommentProblemsCollector>(docCommentBlock.Language);
      if (errorsCollector.Run(docCommentBlock) is { Count: > 0 } errors)
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

    var builder = LanguageManager.TryGetService<IGroupOfLineCommentsCreator>(commentNode.Language);
    if (builder?.TryCreate(commentNode) is not var (groupOfLineComments, includedCommentsNodes)) return;
    
    Comments.Add(CommentProcessingResult.CreateSuccess(groupOfLineComments));
    VisitedNodes.AddRange(includedCommentsNodes);
  }
  
  private void ProcessMultilineComment([NotNull] ICSharpCommentNode commentNode)
  {
    if (ProcessKind is not DaemonProcessKind.VISIBLE_DOCUMENT) return;
    
    var builder = LanguageManager.TryGetService<IMultilineCommentsBuilder>(commentNode.Language);
    if (builder?.Build(commentNode) is { } multilineComment)
    {
      Comments.Add(CommentProcessingResult.CreateSuccess(multilineComment));
    }
  }
}