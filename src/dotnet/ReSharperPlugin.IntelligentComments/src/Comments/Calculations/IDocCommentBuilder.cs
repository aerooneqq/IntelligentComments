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
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

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
      : this(stack, ContentSegments.CreateEmpty(), logger)
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
      var segments = contentSegments.Segments;

      void Normalize()
      {
        foreach (var segment in segments)
        {
          if (segment is ITextContentSegment textContentSegment)
          {
            textContentSegment.Normalize();
          }
        }
      }
        
      int index = 0;
      while (index != segments.Count)
      {
        if (index + 1 >= segments.Count)
        {
          Normalize();
          return;
        }

        var currentSegment = segments[index];
        var nextSegment = segments[index + 1];
        if (currentSegment is not IMergeableContentSegment currentTextSegment ||
            nextSegment is not IMergeableContentSegment nextTextSegment)
        {
          ++index;
          continue;
        }
          
        currentTextSegment.MergeWith(nextTextSegment);
        segments.RemoveAt(index + 1);
      }
        
      Normalize();
    }
  }

  private const string UndefinedParam = "???";
  private const string CRef = "cref";
  private const string Href = "href";
  
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
      if (owner.GetXMLDoc(true) is not { } xmlNode)
      {
        return null;
      }

      var topmostContentSegments = ContentSegments.CreateEmpty();
      using (new WithPushedToStackContentSegments(myContentSegmentsStack, topmostContentSegments, ourLogger))
      {
        Visit(xmlNode);
      }
        
      var content = new IntelligentCommentContent(topmostContentSegments);
      return new DocComment(content, owner.FirstChild.CreatePointer());
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
    if (ElementHasOneTextChild(element, out var value))
    {
      value = PreprocessText(value);
      var highlighter = myHighlightersProvider.GetCXmlElementHighlighter(0, value.Length);
      AddHighlightedText(value, highlighter);
      
      return;
    }
      
    ExecuteActionOverChildren(element, Visit);
  }

  private static bool ElementHasOneTextChild([NotNull] XmlElement element, [NotNull] out string value)
  {
    bool hasOneTextChild = element.ChildNodes.Count == 1 && element.FirstChild is XmlText { Value: { } };

    value = hasOneTextChild switch
    {
      true => element.FirstChild.Value,
      false => string.Empty
    };

    return hasOneTextChild;
  }

  private void AddHighlightedText([NotNull] string text, [NotNull] TextHighlighter highlighter)
  {
    var highlightedText = new HighlightedText(text, new[] { highlighter });
    var textContentSegment = new MergeableTextContentSegment(highlightedText);
    ExecuteWithTopmostContentSegments(segments => segments.Segments.Add(textContentSegment));
  }

  private static string PreprocessText([NotNull] string text) => text.Replace("\n ", "\n");

  public override void VisitParam(XmlElement element)
  {
    myVisitedNodes.Add(element);
    var paramName = element.GetAttribute("name");
    if (paramName == string.Empty)
    {
      paramName = UndefinedParam;
    }

    var paramSegment = new ParamContentSegment(paramName);
    using (new WithPushedToStackContentSegments(myContentSegmentsStack, paramSegment.ContentSegments, ourLogger))
    {
      ExecuteActionOverChildren(element, Visit);
    }
      
    ExecuteWithTopmostContentSegments(segments => segments.Segments.Add(paramSegment));
  }
    
  private void ExecuteWithTopmostContentSegments([NotNull] Action<IContentSegments> action)
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
    var textContentSegment = new MergeableTextContentSegment(new HighlightedText(PreprocessText(text.Value)));
    ExecuteWithTopmostContentSegments(segments => segments.Segments.Add(textContentSegment));
  }

  public override void VisitPara(XmlElement element)
  {
    var paragraphContentSegment = new ParagraphContentSegment(ContentSegments.CreateEmpty());
    ProcessEntityWithContentSegments(paragraphContentSegment, element);
  }

  private void ProcessEntityWithContentSegments(
    [NotNull] IEntityWithContentSegments entityWithContentSegments, [NotNull] XmlElement element)
  {
    var contentSegments = entityWithContentSegments.ContentSegments;
    using (new WithPushedToStackContentSegments(myContentSegmentsStack, contentSegments, ourLogger))
    {
      ExecuteActionOverChildren(element, Visit);
    }
      
    ExecuteWithTopmostContentSegments(segments => segments.Segments.Add(entityWithContentSegments));
  }

  public override void VisitReturns(XmlElement element)
  {
    myVisitedNodes.Add(element);
    var returnSegment = new ReturnContentSegment(ContentSegments.CreateEmpty());
    ProcessEntityWithContentSegments(returnSegment, element);
  }

  public override void VisitRemarks(XmlElement element)
  {
    myVisitedNodes.Add(element);
    var remarksSegment = new RemarksContentSegment(ContentSegments.CreateEmpty());
    ProcessEntityWithContentSegments(remarksSegment, element);
  }

  public override void VisitException(XmlElement element)
  {
    myVisitedNodes.Add(element);
    string exceptionName = UndefinedParam;
    if (element.GetAttributeNode(CRef) is { } attribute)
    {
      myVisitedNodes.Add(attribute);
      exceptionName = attribute.Value;
    }
      
    var exceptionSegment = new ExceptionContentSegment(exceptionName);
    ProcessEntityWithContentSegments(exceptionSegment, element);
  }

  public override void VisitParamRef(XmlElement element)
  {
    myVisitedNodes.Add(element);
    var paramName = element.GetAttribute("name");
    if (paramName == string.Empty) paramName = UndefinedParam;
    paramName = PreprocessText(paramName);

    var highlighter = myHighlightersProvider.GetParamRefElementHighlighter(0, paramName.Length);
    AddHighlightedText(paramName, highlighter);
  }

  public override void VisitSeeAlso(XmlElement element)
  {
    myVisitedNodes.Add(element);
    var href = element.GetAttribute(Href);
    if (href != string.Empty)
    {
      VisitSeeAlsoLink(element);
      return;
    }
    
    VisitSeeAlsoMember(element);
  }

  private void VisitSeeAlsoLink(XmlElement element)
  {
    ProcessSeeAlso(element, Href, (referenceRawText, description) =>
    {
      var highlighter = myHighlightersProvider.GetSeeAlsoLinkHighlighter(0, description.Length);
      var highlightedText = new HighlightedText(description, new[] { highlighter });
      return new SeeAlsoLinkContentSegment(highlightedText, new HttpReference(referenceRawText));
    });
  }

  private void ProcessSeeAlso(
    [NotNull] XmlElement element,
    [NotNull] string attributeName,
    [NotNull] Func<string, string, ISeeAlsoContentSegment> factory)
  {
    var attributeValue = element.GetAttribute(attributeName);
    var innerText = attributeValue;
    if (ElementHasOneTextChild(element, out var text))
    {
      innerText = text;
    }

    var seeAlso = factory.Invoke(attributeValue, innerText);
    ExecuteWithTopmostContentSegments(segments => segments.Segments.Add(seeAlso));
  }

  private void VisitSeeAlsoMember(XmlElement element)
  {
    ProcessSeeAlso(element, CRef, (referenceRawText, description) =>
    {
      var highlighter = myHighlightersProvider.GetSeeAlsoMemberHighlighter(0, description.Length);
      var highlightedText = new HighlightedText(description, new[] { highlighter });
      return new SeeAlsoMemberContentSegment(highlightedText, new CodeEntityReference(referenceRawText));
    });
  }
}