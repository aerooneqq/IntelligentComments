using System.Collections.Generic;
using System.Xml;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;

[Language(typeof(CSharpLanguage))]
public class CSharpInvariantsProcessor : TreeNodeVisitor<Dictionary<string, int>>, IInvariantsProcessor
{
  public void Process(IFile file, Dictionary<string, int> invariantsCount)
  {
    if (file is not ICSharpFile cSharpFile) return;
    foreach (var comment in cSharpFile.Descendants<IDocCommentBlock>().Collect())
    {
      if (comment.GetXML(null) is not { } xml) return;

      for (var node = xml.FirstChild; node is { }; node = node.NextSibling)
      {
        if (node is not XmlElement xmlElement ||
            CommentsBuilderUtil.TryGetInvariantName(xmlElement) is not { } invariantName)
        {
          continue;
        }

        if (invariantsCount.ContainsKey(invariantName))
        {
          ++invariantsCount[invariantName];
        }
        else
        {
          invariantsCount[invariantName] = 1;
        }
      }
    }
  }
}