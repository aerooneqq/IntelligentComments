using System;
using System.Collections.Generic;
using System.Xml;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Util;

namespace IntelligentComments.Comments.Calculations.Core.DocComments;

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
    myAdditionalHandlers = new Dictionary<string, Action<XmlElement>>
    {
      [DocCommentsBuilderUtil.ImageTagName] = VisitImage,
      [DocCommentsBuilderUtil.ReferenceTagName] = VisitReference,
      [DocCommentsBuilderUtil.InvariantTagName] = VisitInvariant,
      [DocCommentsBuilderUtil.TodoTagName] = VisitTodo,
      [DocCommentsBuilderUtil.HackTagName] = VisitHack,
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

  protected abstract void VisitHack([NotNull] XmlElement element);
  protected abstract void VisitImage([NotNull] XmlElement element);
  protected abstract void VisitInvariant([NotNull] XmlElement element);
  protected abstract void VisitReference([NotNull] XmlElement element);
  protected abstract void VisitTodo([NotNull] XmlElement element);
}