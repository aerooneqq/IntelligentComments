using System.Linq;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;

namespace IntelligentComments.Comments.Completion;

internal static class CommentsCompletionExtensions 
{
  [CanBeNull]
  public static ITokenNode TryGetXmlToken([NotNull] this IDocCommentBlock docCommentBlock, DocumentOffset caretDocumentOffset)
  {
    var psiHelper = LanguageManager.Instance.GetService<IPsiHelper>(docCommentBlock.Language);
    if (psiHelper.GetXmlDocPsi(docCommentBlock)?.XmlFile is not { } xmlFile) return null;

    var adjustedOffset = xmlFile.Translate(caretDocumentOffset);

    return xmlFile.FindTokenAt(adjustedOffset) as ITokenNode;
  }
  
  [CanBeNull]
  public static IDocCommentBlock TryFindDocCommentBlock([NotNull] this ITreeNode node)
  {
    while (node is { } and not IDocCommentBlock)
    {
      node = node.Parent;
    }
    
    return node as IDocCommentBlock;
  }
  
  [CanBeNull]
  internal static TextLookupRanges TryGetAttributeValueRanges([NotNull] ITokenNode contextToken)
  {
    if (contextToken.Parent is not IXmlAttribute parentAttribute) return null;

    var eq = parentAttribute.Eq;
    if (eq is null || !eq.RightSiblings().Contains(contextToken)) return null;

    if (contextToken is not IXmlValueToken) return null;
    
    var range = contextToken.GetDocumentRange().TrimLeft(1).TrimRight(1);
    if (!range.IsValid()) return null;
    
    return new TextLookupRanges(range, range);
  }
}