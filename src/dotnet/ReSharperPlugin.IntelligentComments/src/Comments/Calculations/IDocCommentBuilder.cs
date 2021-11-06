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

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations
{
  public interface IDocCommentBuilder
  {
    [CanBeNull] IDocComment Build(IXmlDocOwnerTreeNode node);
  }

  public class DocCommentBuilder : XmlDocVisitor, IDocCommentBuilder
  {
    private readonly struct WithPushedToStackContentSegments : IDisposable
    {
      [NotNull] private readonly Stack<IContentSegments> myStack;
      [NotNull] private readonly ILogger myLogger;

      
      public WithPushedToStackContentSegments([NotNull] Stack<IContentSegments> stack, [NotNull] ILogger logger)
        : this(stack, new ContentSegments(new List<IContentSegment>()), logger)
      {
      }
      
      public WithPushedToStackContentSegments(
        [NotNull] Stack<IContentSegments> stack, [NotNull] IContentSegments segmentsToPush, ILogger logger)
      {
        myStack = stack;
        myLogger = logger;
        myStack.Push(segmentsToPush);
      }
      
      
      public void Dispose()
      {
        if (myStack.Count == 0)
        {
          myLogger.LogAssertion("Stack was empty before possible Pop()");
          return;
        }
        
        var contentSegments = myStack.Pop();
        int index = 0;
        while (index != contentSegments.Segments.Count)
        {
          if (index + 1 >= contentSegments.Segments.Count) return;

          var currentSegment = contentSegments.Segments[index];
          var nextSegment = contentSegments.Segments[index + 1];
          if (currentSegment is not IMergeableContentSegment currentTextSegment ||
              nextSegment is not IMergeableContentSegment nextTextSegment)
          {
            ++index;
            continue;
          }
          
          currentTextSegment.MergeWith(nextTextSegment);
          contentSegments.Segments.RemoveAt(index + 1);
        }
      }
    }

    private const string UndefinedParamName = "???";
    
    [NotNull] private static readonly ILogger ourLogger = Logger.GetLogger<DocCommentBuilder>();
  
    [NotNull] private readonly Stack<IContentSegments> myContentSegmentsStack;
    [NotNull] private readonly ISet<XmlNode> myVisitedNodes;
    [NotNull] private readonly IHighlightersProvider myHighlightersProvider;


    public DocCommentBuilder([NotNull] IHighlightersProvider highlightersProvider)
    {
      myHighlightersProvider = highlightersProvider;
      myContentSegmentsStack = new Stack<IContentSegments>();
      myContentSegmentsStack.Push(new ContentSegments(new List<IContentSegment>()));
      
      myVisitedNodes = new HashSet<XmlNode>();
    }
    

    public IDocComment Build(IXmlDocOwnerTreeNode owner)
    {
      try
      {
        if (owner.GetXMLDoc(true) is not { } xmlNode) return null;

        var topmostContentSegments = new ContentSegments(new List<IContentSegment>());
        using (new WithPushedToStackContentSegments(myContentSegmentsStack, topmostContentSegments, ourLogger))
        {
          Visit(xmlNode);
        }
        
        var content = new IntelligentCommentContent(topmostContentSegments);
        return new DocComment(content, owner.CreatePointer());
      }
      catch (Exception ex)
      {
        ourLogger.LogException(ex);
        return null;
      }
    }
    

    public override void Visit(XmlNode node)
    {
      if (myVisitedNodes.Contains(node)) return;
      base.Visit(node);
    }

    public override void VisitSummary(XmlElement element)
    {
      myVisitedNodes.Add(element);
      ExecuteActionOverChildren(element, Visit);
    }
    
    private static void ExecuteActionOverChildren(XmlElement parent, Action<XmlNode> actionWithNode)
    {
      foreach (var child in parent)
      {
        if (child is not XmlNode childXmlNode)
        {
          ourLogger.LogAssertion($"Child is not of expected type ({child?.GetType().Name})");
          continue;
        }

        actionWithNode(childXmlNode);
      }
    }

    public override void VisitC(XmlElement element)
    {
      myVisitedNodes.Add(element);
      if (element.ChildNodes.Count == 1 && element.FirstChild is XmlText { Value: { } value })
      {
        var highlighter = myHighlightersProvider.GetCXmlElementHighlighter(0, value.Length);
        var highlightedText = new HighlightedText(value, new[] { highlighter });
        var textContentSegment = new MergeableTextContentSegment(highlightedText);
        ExecuteWithTopmostContentSegments(segments => segments.Segments.Add(textContentSegment));

        return;
      }
      
      ExecuteActionOverChildren(element, Visit);
    }

    public override void VisitParam(XmlElement element)
    {
      myVisitedNodes.Add(element);
      var paramName = element.GetAttribute("name");
      if (paramName == string.Empty)
      {
        paramName = UndefinedParamName;
      }

      var paramSegment = new ParamContentSegment(paramName);
      using (new WithPushedToStackContentSegments(myContentSegmentsStack, paramSegment.ContentSegments, ourLogger))
      {
        ExecuteActionOverChildren(element, Visit);
      }
      
      ExecuteWithTopmostContentSegments(segments => segments.Segments.Add(paramSegment));
    }
    
    private void ExecuteWithTopmostContentSegments(Action<IContentSegments> action)
    {
      if (myContentSegmentsStack.Count == 0)
      {
        ourLogger.LogAssertion("Trying to get content segments when stack is empty");
        using (new WithPushedToStackContentSegments(myContentSegmentsStack, ourLogger))
        {
          action(myContentSegmentsStack.Peek());
        }
      }

      action(myContentSegmentsStack.Peek());
    }

    public override void VisitText(XmlText text)
    {
      myVisitedNodes.Add(text);
      var textContentSegment = new MergeableTextContentSegment(new HighlightedText(text.Value));
      ExecuteWithTopmostContentSegments(segments => segments.Segments.Add(textContentSegment));
    }

    public override void VisitPara(XmlElement element)
    {
      var contentSegments = new ContentSegments(new List<IContentSegment>());
      var paragraphContentSegment = new ParagraphContentSegment(contentSegments);
      
      using (new WithPushedToStackContentSegments(myContentSegmentsStack, contentSegments, ourLogger))
      {
        ExecuteActionOverChildren(element, Visit);
      }
      
      ExecuteWithTopmostContentSegments(segments => segments.Segments.Add(paragraphContentSegment));
    }
  }
}