using System;
using System.Collections.Generic;
using System.Xml;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Util;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;

public abstract class XmlDocVisitorWitCustomElements : XmlDocVisitor
{
  [NotNull] private readonly IDictionary<string, Action<XmlElement>> myAdditionalHandlers;

  [NotNull] protected readonly ISet<XmlNode> VisitedNodes;
  [NotNull] protected readonly IDocCommentBlock InitialComment;

  [NotNull] protected IDocCommentBlock AdjustedComment { get; set; }


  // ReSharper disable once NotNullMemberIsNotInitialized
  protected XmlDocVisitorWitCustomElements([NotNull] IDocCommentBlock comment)
  {
    InitialComment = comment;
    VisitedNodes = new HashSet<XmlNode>();
    myAdditionalHandlers = new Dictionary<string, Action<XmlElement>>()
    {
      [CommentsBuilderUtil.ImageTagName] = VisitImage,
      [CommentsBuilderUtil.ReferenceTagName] = VisitReference,
      [CommentsBuilderUtil.InvariantTagName] = VisitInvariant
    };
  }
  

  public sealed override void VisitUnknownTag(XmlElement element)
  {
    VisitedNodes.Add(element);
    if (myAdditionalHandlers.TryGetValue(element.LocalName, out var handler))
    {
      handler?.Invoke(element);
    }
  }

  protected abstract void VisitImage(XmlElement element);
  protected abstract void VisitInvariant(XmlElement element);
  protected abstract void VisitReference(XmlElement element);
}