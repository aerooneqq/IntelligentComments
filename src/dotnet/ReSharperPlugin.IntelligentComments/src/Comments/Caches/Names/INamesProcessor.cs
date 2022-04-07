using System;
using System.Collections.Generic;
using System.Xml;
using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Names;

public interface INamesProcessor
{
  void Process([NotNull] IFile file, [NotNull] Dictionary<string, int> namesCount);
}

public class NamesProcessor : INamesProcessor
{
  private readonly NameKind myWantedNameKind;

  
  public NamesProcessor(NameKind wantedNameKind)
  {
    myWantedNameKind = wantedNameKind;
  }
  
  
  public void Process(IFile file, Dictionary<string, int> namesCount)
  {
    foreach (var comment in file.Descendants<IDocCommentBlock>().Collect())
    {
      comment.ExecuteActionWithNames((extraction, _) =>
      {
        if (extraction.NameKind != myWantedNameKind) return;

        var extractionName = extraction.Name;
        Assertion.AssertNotNull(extractionName, "invariantName != null");

        if (extractionName.IsNullOrWhitespace()) return;
        
        if (namesCount.ContainsKey(extractionName))
        {
          ++namesCount[extractionName];
        }
        else
        {
          namesCount[extractionName] = 1;
        }
      });
    }
  }
}

public static class CSharpInvariantsProcessorExtensions
{
  public static void ExecuteActionWithNames(
    [NotNull] this IDocCommentBlock commentBlock, [NotNull] Action<NameExtraction, IXmlTag> actionWithInvariant)
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