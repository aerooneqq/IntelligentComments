using System.Linq;
using IntelligentComments.Comments.Calculations.Core.DocComments.Errors;
using JetBrains.Annotations;
using JetBrains.Application;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;

namespace IntelligentComments.Comments.Calculations.Core.Languages.CSharp;

/// <summary>
/// Specification:
/// 1. Multiline comments
/// 2. Single line comments
/// 3. Group of single line comments
/// 4. Documentation comments
/// 5. ReSharper disable [InspectionName]
/// 6. reference to invariant: [Invariant name]
/// </summary>
[Language(typeof(CSharpLanguage))]
public class CSharpCommentsProcessor : CommentsProcessorBase
{
  protected override void ProcessBeforeInteriorInternal(ITreeNode element, CommentsProcessorContext context)
  {
    Interruption.Current.CheckAndThrow();
    
    if (element is ICSharpDocCommentBlock docCommentBlock)
    {
      ProcessDocCommentBlock(docCommentBlock, context);
      return;
    }
    
    foreach (var operation in CommentOperationsUtil.CollectOperations(element))
    {
      if (operation.TryCreate(element) is var (comment, nodes))
      {
        context.VisitedNodes.AddRange(nodes);
        var errors = operation.FindErrors(element).Select(error => new HighlightingInfo(error.CalculateRange(), error)).ToList();
        if (errors.Count > 0)
        {
          context.ProcessedComments.Add(CommentProcessingResult.CreateWithErrors(errors, element.Language, comment.Range));
          return;
        }

        if (context.ProcessKind == DaemonProcessKind.VISIBLE_DOCUMENT)
        {
          context.ProcessedComments.Add(CommentProcessingResult.CreateSuccess(comment));          
        }

        return;
      }
    }
  }

  private void ProcessDocCommentBlock([NotNull] ICSharpDocCommentBlock docCommentBlock, CommentsProcessorContext context)
  {
    var errorsCollector = LanguageManager.Instance.GetService<IDocCommentProblemsCollector>(docCommentBlock.Language);
    if (errorsCollector.Run(docCommentBlock) is { Count: > 0 } errors)
    {
      var range = docCommentBlock.GetDocumentRange();
      context.ProcessedComments.Add(CommentProcessingResult.CreateWithErrors(errors, CSharpLanguage.Instance!, range));
      return;
    }
    
    if (context.ProcessKind is DaemonProcessKind.VISIBLE_DOCUMENT)
    {
      var builder = new CSharpDocCommentBuilder(docCommentBlock);

      if (builder.Build() is { } comment)
      {
        context.ProcessedComments.Add(CommentProcessingResult.CreateSuccess(comment));
      } 
    }
  }
}