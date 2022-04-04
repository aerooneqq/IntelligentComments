using System;
using System.Collections.Generic;
using System.Xml;
using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;

[Language(typeof(CSharpLanguage))]
public class CSharpInvariantsProcessor : TreeNodeVisitor<Dictionary<string, int>>, IInvariantsProcessor
{
  public void Process(IFile file, Dictionary<string, int> invariantsCount)
  {
    if (file is not ICSharpFile cSharpFile) return;
    foreach (var comment in cSharpFile.Descendants<IDocCommentBlock>().Collect())
    {
      comment.ExecuteActionWithInvariants(element =>
      {
        var invariantName = DocCommentsBuilderUtil.TryGetInvariantName(element);
        Assertion.AssertNotNull(invariantName, "invariantName != null");

        if (invariantName.IsNullOrWhitespace()) return;
        
        if (invariantsCount.ContainsKey(invariantName))
        {
          ++invariantsCount[invariantName];
        }
        else
        {
          invariantsCount[invariantName] = 1;
        }
      });
    }
  }
}

public static class CSharpInvariantsProcessorExtensions
{
  public static void ExecuteActionWithInvariants(
    [NotNull] this IDocCommentBlock commentBlock, [NotNull] Action<XmlElement> actionWithInvariant)
  {
    if (commentBlock.GetXML(null) is not { } xml) return;
    
    for (var node = xml.FirstChild; node is { }; node = node.NextSibling)
    {
      if (node is not XmlElement xmlElement ||
          DocCommentsBuilderUtil.TryGetInvariantName(xmlElement) is not { })
      {
        continue;
      }

      actionWithInvariant(xmlElement);
    }
  }
  
  public static void ExecuteWithReferences(
    [NotNull] this IDocCommentBlock commentBlock, [NotNull] Action<IXmlTag> actionWithReference)
  {
    var psiHelper = LanguageManager.Instance.TryGetService<IPsiHelper>(commentBlock.Language);
    if (psiHelper?.GetXmlDocPsi(commentBlock) is not { } xmlDocPsi) return;

    foreach (var tag in xmlDocPsi.XmlFile.Descendants<IXmlTag>().Collect())
    {
      if (DocCommentsBuilderUtil.IsReferenceTagWithInvariantSource(tag))
      {
        actionWithReference(tag);
      }
    }
  }
}