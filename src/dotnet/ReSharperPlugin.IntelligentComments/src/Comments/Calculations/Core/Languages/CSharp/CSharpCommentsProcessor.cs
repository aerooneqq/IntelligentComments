using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Errors;

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
    if (element is ICSharpDocCommentBlock docCommentBlock)
    {
      ProcessDocCommentBlock(docCommentBlock);
      return;
    }
    
    var creators = LanguageManager
      .TryGetCachedServices<ICommentFromNodeCreator>(element.Language)
      .OrderByDescending(creator => creator.Priority);
    
    foreach (var creator in creators)
    {
      if (creator.TryCreate(element) is var (comment, nodes))
      {
        VisitedNodes.AddRange(nodes);
        Comments.Add(CommentProcessingResult.CreateSuccess(comment));
        return;
      }
    }
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
}