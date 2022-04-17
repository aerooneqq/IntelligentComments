using System;
using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.ExtensionsAPI.Tree;
using JetBrains.ReSharper.Psi.Parsing;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Parsing;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.Text;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.Features.Rename;

public static class RenameUtil
{
  public static IXmlValueToken FindAttributeValueToken(
    [NotNull] IDocCommentBlock docCommentBlock, DocumentRange declarationRange)
  {
    if (LanguageManager.Instance.TryGetService<IPsiHelper>(docCommentBlock.Language) is not { } helper) return null;
    if (helper.GetXmlDocPsi(docCommentBlock) is not { XmlFile: { } xmlFile }) return null;

    var docRange = xmlFile.Translate(declarationRange.StartOffset);
    if (xmlFile.FindTokenAt(docRange) is not IXmlValueToken { Parent: IXmlAttribute } valueToken)
    {
      return null;
    }

    return valueToken;
  }

  [NotNull]
  public static IXmlAttributeValue ReplaceAttributeValue([NotNull] IXmlValueToken valueToken, [NotNull] string newName)
  {
    var factory = XmlTreeNodeFactory.GetInstance(valueToken);
    var buffer = new StringBuffer($"\"{newName}\"");
    var xmlTokenTypes = XmlTokenTypes.GetInstance(valueToken.Language);
    var newValue = factory.CreateAttributeValue(xmlTokenTypes.STRING, buffer, 0, buffer.Length);

    using (WriteLockCookie.Create(valueToken.IsPhysical()))
    {
      ModificationUtil.ReplaceChild(valueToken, newValue);
    }
    
    return newValue;
  }

  public static void ReplaceReferenceCommentNode(
    [NotNull] ICommentNode commentNode, 
    NameWithKind oldName, 
    [NotNull] string newName)
  {
    if (TryGetNeededRange(commentNode, finder => finder.FindReferences(commentNode, oldName)) is not { } referenceRange) 
      return;
    
    ReplaceCommentNodeInternal(commentNode, referenceRange, newName);
  }

  private static void ReplaceCommentNodeInternal(
    [NotNull] ICommentNode commentNode,
    DocumentRange entityInCommentRange,
    [NotNull] string newName)
  {
    var startOffset = commentNode.GetDocumentRange().StartOffset;
    var nameOffset = entityInCommentRange.StartOffset - startOffset;
    var nameEndOffset = entityInCommentRange.EndOffset - startOffset;
    var text = commentNode.GetText();
    var newText = text[..nameOffset] + newName;
    if (nameEndOffset < text.Length)
    {
      newText += text[nameEndOffset..];
    }

    var newCommentNode = CSharpElementFactory.GetInstance(commentNode).CreateComment(newText);

    using (WriteLockCookie.Create(commentNode.IsPhysical()))
    {
      ModificationUtil.ReplaceChild(commentNode, newCommentNode);
    }
  }

  public static DocumentRange? TryGetNeededRange(
    ICommentNode commentNode,
    [NotNull] Func<INamedEntitiesCommonFinder, IEnumerable<CommonNamedEntityDescriptor>> descriptorsExtractor)
  {
    var finders = LanguageManager.Instance.TryGetCachedServices<INamedEntitiesCommonFinder>(commentNode.Language);
    foreach (var finder in finders)
    {
      if (descriptorsExtractor(finder) is { } references &&
          references.FirstOrDefault() is { } firstReference &&
          commentNode.GetDocumentRange().Contains(firstReference.EntityRange))
      {
        return firstReference.EntityRange;
      }
    }

    return null;
  }

  [CanBeNull]
  public static DocumentRange? ReplaceNameCommentNode(
    [NotNull] ICommentNode commentNode, 
    [NotNull] string newName)
  {
    if (TryGetNeededRange(commentNode, finder => finder.FindNames(commentNode)) is not { } nameRange) return null;
    ReplaceCommentNodeInternal(commentNode, nameRange, newName);

    return nameRange.StartOffset.ExtendRight(newName.Length);
  }
}