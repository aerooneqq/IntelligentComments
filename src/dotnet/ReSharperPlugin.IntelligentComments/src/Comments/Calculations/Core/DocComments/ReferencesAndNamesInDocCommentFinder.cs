using System;
using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

[Language(typeof(KnownLanguage))]
public class ReferencesAndNamesInDocCommentFinder : INamedEntitiesCommonFinder
{
  public IEnumerable<CommonNamedEntityDescriptor> FindReferences(ITreeNode node, NameWithKind nameWithKind)
  {
    return FindReferencesToNamedEntityOrAll(node, nameWithKind);
  }

  private static IEnumerable<CommonNamedEntityDescriptor> FindReferencesToNamedEntityOrAll(
    [NotNull] ITreeNode node,
    NameWithKind? nameWithKind)
  {
    if (node is not IDocCommentBlock docComment || node.GetSourceFile() is not { } sourceFile)
    {
      return EmptyList<CommonNamedEntityDescriptor>.Enumerable;
    }

    var references = new LocalList<CommonNamedEntityDescriptor>();
    ExecuteWithReferences(docComment, referenceTag =>
    {
      var extraction = DocCommentsBuilderUtil.TryExtractOneReferenceNameKindFromReferenceTag(referenceTag);
      if (extraction is null || (nameWithKind.HasValue && extraction != nameWithKind.Value)) return;
      if (DocCommentsBuilderUtil.TryGetOneReferenceSourceAttribute(referenceTag) is not { } sourceAttribute) return;
      
      references.Add(new CommonNamedEntityDescriptor(sourceFile, sourceAttribute.Value.GetDocumentRange(), extraction.Value));
    });

    return references.ResultingList();
  }

  public IEnumerable<CommonNamedEntityDescriptor> FindAllReferences([NotNull] ITreeNode node)
  {
    return FindReferencesToNamedEntityOrAll(node, null);
  }

  public IEnumerable<CommonNamedEntityDescriptor> FindNames(ITreeNode node)
  {
    if (node is not IDocCommentBlock docCommentBlock) return EmptyList<CommonNamedEntityDescriptor>.Enumerable;
    if (node.GetSourceFile() is not { } sourceFile) return EmptyList<CommonNamedEntityDescriptor>.Enumerable;
    
    var names = new LocalList<CommonNamedEntityDescriptor>();
    ExecuteActionWithNames(docCommentBlock, (kind, tag) =>
    {
      if (DocCommentsBuilderUtil.TryGetCommonNameAttribute(tag) is not { } attribute) return;
      names.Add(new CommonNamedEntityDescriptor(sourceFile, attribute.Value.GetDocumentRange(), kind));
    });

    return names.ResultingList();
  }
  
  public static void ExecuteActionWithNames(
    [NotNull] IDocCommentBlock commentBlock, [NotNull] Action<NameWithKind, IXmlTag> actionWithTag)
  {
    if (LanguageManager.Instance.TryGetService<IPsiHelper>(commentBlock.Language) is not { } psiHelper) return;
    if (psiHelper.GetXmlDocPsi(commentBlock) is not { } xmlDocPsi) return;

    var xmlFile = xmlDocPsi.XmlFile;
    for (var node = xmlFile.FirstChild; node is { }; node = node.NextSibling)
    {
      if (node is not IXmlTag xmlTag ||
          DocCommentsBuilderUtil.TryExtractNameFrom(xmlTag) is not { } nameExtraction)
      {
        continue;
      }

      actionWithTag(nameExtraction, xmlTag);
    }
  }
  
  private static void ExecuteWithReferences(
    [NotNull] IDocCommentBlock commentBlock, [NotNull] Action<IXmlTag> actionWithReference)
  {
    var psiHelper = LanguageManager.Instance.TryGetService<IPsiHelper>(commentBlock.Language);
    if (psiHelper?.GetXmlDocPsi(commentBlock) is not { } xmlDocPsi) return;

    foreach (var tag in xmlDocPsi.XmlFile.Descendants<IXmlTag>().Collect())
    {
      if (DocCommentsBuilderUtil.IsReferenceTag(tag))
      {
        actionWithReference(tag);
      }
    }
  }
}