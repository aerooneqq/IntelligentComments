using JetBrains.Annotations;
using JetBrains.Application.Parts;
using JetBrains.Diagnostics;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;

namespace IntelligentComments.Comments.Completion;

[IntellisensePart(Instantiation.DemandAnyThreadSafe)]
public class DocCommentCompletionContextProvider : ICodeCompletionContextProvider
{
  [NotNull] private static readonly Key<IDocCommentBlock> ourDocCommentKey = new(nameof(ourDocCommentKey)); 
  
  
  public bool IsApplicable(CodeCompletionContext context)
  {
    return TryGetDocCommentBlock(context) is { };
  }
  
  [CanBeNull]
  private static IDocCommentBlock TryGetDocCommentBlock(CodeCompletionContext context)
  {
    var treeOffset = context.File.Translate(context.CaretDocumentOffset);
    var node = context.File.FindTokenAt(treeOffset);
    var docCommentBlock = node?.TryFindDocCommentBlock();
    
    context.PutData(ourDocCommentKey, docCommentBlock);
    return docCommentBlock;
  }

  public ISpecificCodeCompletionContext GetCompletionContext(CodeCompletionContext context)
  {
    var docCommentBlock = context.GetData(ourDocCommentKey);
    Assertion.AssertNotNull(docCommentBlock);

    if (docCommentBlock.TryGetXmlToken(context.CaretDocumentOffset) is not { } contextDocCommentNode) return null;
    
    return new DocCommentCompletionContext(context, contextDocCommentNode, docCommentBlock);
  }
}