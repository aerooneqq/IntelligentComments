using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpCommentsProcessor : CommentsProcessorBase
{
  public override void ProcessBeforeInterior(ITreeNode element)
  {
    if (VisitedComments.Contains(element)) return;
    
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

  private void ProcessDocCommentBlock([NotNull] ICSharpDocCommentBlock docCommentBlock)
  {
    VisitedComments.Add(docCommentBlock);

    var errorsCollector = new CSharpCommentProblemsCollector(docCommentBlock);
    if (errorsCollector.Run() is { Count: > 0 } errors)
    {
      var range = docCommentBlock.GetDocumentRange();
      Comments.Add(CommentProcessingResult.CreateWithErrors(errors, CSharpLanguage.Instance!, range));
      return;
    }
    
    var builder = new CSharpDocCommentBuilder(docCommentBlock);

    if (builder.Build() is { } comment)
    {
      Comments.Add(CommentProcessingResult.CreateWithoutErrors(comment));
    }
  }

  private void ProcessLineComment([NotNull] ICSharpCommentNode commentNode)
  {
    var builder = new CSharpGroupOfLineCommentsBuilder(commentNode);

    VisitedComments.Add(commentNode);

    if (builder.Build() is not var (groupOfLineComments, includedCommentsNodes)) return;
    
    Comments.Add(CommentProcessingResult.CreateWithoutErrors(groupOfLineComments));
    VisitedComments.AddRange(includedCommentsNodes);
  }

  private void ProcessMultilineComment([NotNull] ICSharpCommentNode commentNode)
  {
    var builder = new CSharpMultilineCommentBuilder(commentNode);
    
    VisitedComments.Add(commentNode);
    
    if (builder.Build() is { } multilineComment)
    {
      Comments.Add(CommentProcessingResult.CreateWithoutErrors(multilineComment));
    }
  }
}