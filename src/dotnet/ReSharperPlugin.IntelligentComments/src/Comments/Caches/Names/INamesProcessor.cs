using System;
using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Names;

public record struct NamedEntityInfo(DocumentOffset Offset);

public interface INamesProcessor
{
  void Process([NotNull] IFile file, [NotNull] Dictionary<string, List<NamedEntityInfo>> namesInfo);
}

public class NamesProcessor : INamesProcessor, IRecursiveElementProcessor<Dictionary<string, List<NamedEntityInfo>>>
{
  private readonly NameKind myWantedNameKind;

  
  
  public NamesProcessor(NameKind wantedNameKind)
  {
    myWantedNameKind = wantedNameKind;
  }
  
  
  public void Process(IFile file, Dictionary<string, List<NamedEntityInfo>> namesInfo)
  {
    file.ProcessThisAndDescendants(this, namesInfo);
  }

  public void ProcessBeforeInterior(ITreeNode element, Dictionary<string, List<NamedEntityInfo>> context)
  {
    if (element is IDocCommentBlock comment)
    {
      ProcessDocComment(comment, context);
      return;
    }

    if (element is ICommentNode commentNode)
    {
      if (element.TryFindDocCommentBlock() is { }) return;
      
      var finders = LanguageManager.Instance.TryGetCachedServices<INamedEntitiesCommonFinder>(element.Language);
      foreach (var finder in finders)
      {
        foreach (var descriptor in finder.FindNames(commentNode))
        {
          if (descriptor.NameWithKind.NameKind != myWantedNameKind) continue;
          
          var infos = context.GetOrCreateValue(descriptor.NameWithKind.Name, static () => new List<NamedEntityInfo>());
          infos.Add(new NamedEntityInfo(commentNode.GetDocumentStartOffset()));
        }
      }
    }
  }

  private void ProcessDocComment(IDocCommentBlock comment, Dictionary<string, List<NamedEntityInfo>> context)
  {
    comment.ExecuteActionWithNames((extraction, xmlTag) =>
    {
      if (extraction.NameKind != myWantedNameKind) return;

      var extractionName = extraction.Name;
      Assertion.AssertNotNull(extractionName, "invariantName != null");

      if (extractionName.IsNullOrWhitespace()) return;

      var infos = context.GetOrCreateValue(extractionName, static () => new List<NamedEntityInfo>());
      infos.Add(new NamedEntityInfo(xmlTag.GetDocumentStartOffset()));
    });
  }

  public void ProcessAfterInterior(ITreeNode element, Dictionary<string, List<NamedEntityInfo>> context)
  {
  }
  
  public bool InteriorShouldBeProcessed(ITreeNode element, Dictionary<string, List<NamedEntityInfo>> context) => true;
  public bool IsProcessingFinished(Dictionary<string, List<NamedEntityInfo>> context) => false;
}

public static class CSharpNamesProcessorExtensions
{
  public static void ExecuteActionWithNames(
    [NotNull] this IDocCommentBlock commentBlock, [NotNull] Action<NameWithKind, IXmlTag> actionWithInvariant)
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

      actionWithInvariant(nameExtraction, xmlTag);
    }
  }
  
  public static void ExecuteWithReferences(
    [NotNull] this IDocCommentBlock commentBlock, [NotNull] Action<IXmlTag> actionWithReference)
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