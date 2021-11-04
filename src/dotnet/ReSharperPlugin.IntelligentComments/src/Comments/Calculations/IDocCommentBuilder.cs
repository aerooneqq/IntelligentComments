using System;
using System.Collections.Generic;
using System.Xml;
using JetBrains.Annotations;
using JetBrains.ReSharper.I18n.Services;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Util;
using JetBrains.Util;
using JetBrains.Util.Logging;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public interface IDocCommentBuilder
{
  [CanBeNull] IDocComment Build(IXmlDocOwnerTreeNode node);
}

public class DocCommentBuilder : XmlDocVisitor, IDocCommentBuilder
{
  [NotNull] private static readonly ILogger ourLogger = Logger.GetLogger<DocCommentBuilder>();
  
  [NotNull] private readonly List<IContentSegment> myContentSegments;
  

  public DocCommentBuilder()
  {
    myContentSegments = new List<IContentSegment>();
  }
  

  public IDocComment Build(IXmlDocOwnerTreeNode owner)
  {
    try
    {
      var xmlNode = owner.GetXMLDoc(true);
      if (xmlNode is null) return null;
      myContentSegments.Clear();
    
      Visit(xmlNode);
      var content = new IntelligentCommentContent(new ContentSegments(myContentSegments));
      return new DocComment(content, owner.CreatePointer());
    }
    catch (Exception ex)
    {
      ourLogger.LogException(ex);
      return null;
    }
  }
  
  
  public override void VisitSummary(XmlElement element)
  {
  }

  public override void VisitText(XmlText text)
  {
    myContentSegments.Add(new TextContentSegment(new HighlightedText(text.InnerText)));
  }
}