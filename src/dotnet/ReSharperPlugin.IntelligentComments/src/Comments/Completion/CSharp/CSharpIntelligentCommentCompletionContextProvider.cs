using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using System.Linq;
using JetBrains.Util;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;

[IntellisensePart]
public class CSharpIntelligentCommentCompletionContextProvider : ICodeCompletionContextProvider
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
    while (node is { } and not IDocCommentBlock)
    {
      node = node.Parent;
    }

    var docCommentBlock = node as IDocCommentBlock;
    context.PutData(ourDocCommentKey, docCommentBlock);
    return docCommentBlock;
  }

  public ISpecificCodeCompletionContext GetCompletionContext(CodeCompletionContext context)
  {
    var docCommentBlock = context.GetData(ourDocCommentKey);
    Assertion.AssertNotNull(docCommentBlock, "docCommentBlock != null");
    
    var psiHelper = LanguageManager.Instance.GetService<IPsiHelper>(docCommentBlock.Language);
    if (psiHelper.GetXmlDocPsi(docCommentBlock)?.XmlFile is not { } xmlFile) return null;

    var offset = xmlFile.Translate(context.CaretDocumentOffset);
    
    if (xmlFile.FindTokenAt(offset) as ITokenNode is not { } contextDocCommentNode) return null;
    if (TryCreateTextLookupRanges(contextDocCommentNode) is not { } ranges) return null;
    if (!ranges.InsertRange.IsValid() || !ranges.ReplaceRange.IsValid()) return null;
    
    return new IntelligentCommentCompletionContext(context, contextDocCommentNode, ranges, docCommentBlock);
  }
  
  [CanBeNull]
  private static TextLookupRanges TryCreateTextLookupRanges([NotNull] ITokenNode contextToken)
  {
    return TryGetAttributeValueRange(contextToken);
  }
  
  [CanBeNull]
  private static TextLookupRanges TryGetAttributeValueRange([NotNull] ITokenNode contextToken)
  {
    if (contextToken.Parent is not IXmlAttribute parentAttribute) return null;

    var eq = parentAttribute.Eq;
    if (eq is null || !eq.RightSiblings().Contains(contextToken)) return null;

    if (contextToken is not IXmlValueToken) return null;
    var range = contextToken.GetDocumentRange().TrimLeft(1).TrimRight(1);
    
    return new TextLookupRanges(range, range);
  }
}