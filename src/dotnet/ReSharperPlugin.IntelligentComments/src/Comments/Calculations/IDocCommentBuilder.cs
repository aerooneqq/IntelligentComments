using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.I18n.Services;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Modules;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Util;
using JetBrains.Rider.Model;
using JetBrains.Util;
using JetBrains.Util.Logging;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;
using ReSharperPlugin.IntelligentComments.Comments.CodeFragmentsHighlighting;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;
using IReference = ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References.IReference;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public interface IDocCommentBuilder
{
  [CanBeNull] IDocComment Build();
}

public class DocCommentBuilder : XmlDocVisitor, IDocCommentBuilder
{
  private record struct ContentSegmentsMetadata(
    [CanBeNull] IEntityWithContentSegments CorrespondingEntity,
    [NotNull] IContentSegments ContentSegments)
  {
    public static ContentSegmentsMetadata CreateEmpty() => new(null, Domain.Impl.Content.ContentSegments.CreateEmpty());
  }

  private readonly struct WithPushedToStackContentSegments : IDisposable
  {
    [NotNull] private readonly Stack<ContentSegmentsMetadata> myStack;
    [NotNull] private readonly ILogger myLogger;

    
    public WithPushedToStackContentSegments([NotNull] Stack<ContentSegmentsMetadata> stack, [NotNull] ILogger logger)
      : this(stack, ContentSegmentsMetadata.CreateEmpty(), logger)
    {
    }
      
    public WithPushedToStackContentSegments(
      [NotNull] Stack<ContentSegmentsMetadata> stack, ContentSegmentsMetadata metadata, ILogger logger)
    {
      myStack = stack;
      myLogger = logger;
      myStack.Push(metadata);
    }
    
      
    public void Dispose()
    {
      if (myStack.Count == 0)
      {
        myLogger.LogAssertion("Stack was empty before possible Pop()");
        return;
      }
        
      var contentSegments = myStack.Pop();
      var segments = contentSegments.ContentSegments.Segments;

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
  
  [NotNull] private const string UndefinedParam = "???";
  [NotNull] private const string CRef = "cref";
  [NotNull] private const string Href = "href";
  [NotNull] private const string LangWord = "langword";

  [NotNull] private static readonly ISet<char> ourWhitespaceChars = new HashSet<char> { ' ', '\n', '\r', '\t' };
  [NotNull] private static readonly ILogger ourLogger = Logger.GetLogger<DocCommentBuilder>();
  [NotNull] private static readonly Func<string, IParamContentSegment> ourParamFactory = name => new ParamContentSegment(name);
  [NotNull] private static readonly Func<string, ITypeParamSegment> ourTypeParamFactory = name => new TypeParamSegment(name);
  
  [NotNull] private readonly Stack<ContentSegmentsMetadata> myContentSegmentsStack;
  [NotNull] private readonly ISet<XmlNode> myVisitedNodes;
  [NotNull] private readonly IHighlightersProvider myHighlightersProvider;
  [NotNull] private readonly IDocCommentBlock myComment;
  [NotNull] private readonly IPsiServices myPsiServices;
  [NotNull] private readonly IPsiModule myPsiModule;
  [NotNull] private readonly CodeFragmentHighlightingManager myCodeFragmentHighlightingManager;
  [NotNull] private readonly ILanguageManager myLanguageManager;
  [NotNull] private readonly IResolveContext myResolveContext;
  [NotNull] private readonly string myDocCommentAttributeId;


  public DocCommentBuilder([NotNull] IDocCommentBlock comment)
  {
    myResolveContext = new ResolveContextImpl(comment.GetSolution());
    myLanguageManager = LanguageManager.Instance;
    myHighlightersProvider = myLanguageManager.GetService<IHighlightersProvider>(comment.Language);
    myComment = comment;
    myContentSegmentsStack = new Stack<ContentSegmentsMetadata>();
    myCodeFragmentHighlightingManager = comment.GetSolution().GetComponent<CodeFragmentHighlightingManager>();
    myPsiServices = myComment.GetPsiServices();
    myPsiModule = myComment.GetPsiModule();
    myVisitedNodes = new HashSet<XmlNode>();
    myDocCommentAttributeId = DefaultLanguageAttributeIds.DOC_COMMENT;
  }
    

  public IDocComment Build()
  {
    try
    {
      if (myComment.GetXML(null) is not { } xmlNode)
      {
        return null;
      }

      var topmostContentSegments = ContentSegmentsMetadata.CreateEmpty();
      using (new WithPushedToStackContentSegments(myContentSegmentsStack, topmostContentSegments, ourLogger))
      {
        Visit(xmlNode);
      }
      
      var content = new IntelligentCommentContent(topmostContentSegments.ContentSegments);
      return new DocComment(content, myComment.CreatePointer());
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
    var summary = new SummaryContentSegment(ContentSegments.CreateEmpty());
    ProcessEntityWithContentSegments(summary, element);
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
    var hasOneTextChild = element.ChildNodes.Count == 1 && element.FirstChild is XmlText { Value: { } };

    value = hasOneTextChild switch
    {
      true => element.FirstChild.Value,
      false => string.Empty
    };

    return hasOneTextChild;
  }

  private void AddHighlightedText([NotNull] string text, [NotNull] TextHighlighter highlighter)
  {
    var highlightedText = new HighlightedText($" {text} ", new[] { highlighter.Shift(1) });
    var textContentSegment = new MergeableTextContentSegment(highlightedText);
    ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(textContentSegment));
  }

  private static string PreprocessText([NotNull] string text)
  {
    var sb = new StringBuilder(text);
    while (ourWhitespaceChars.Contains(sb[0]))
    {
      sb.Remove(0, 1);
    }
      
    while (ourWhitespaceChars.Contains(sb[^1]))
    {
      sb.Remove(sb.Length - 1, 1);
    }
    
    for (int i = sb.Length - 1; i >= 0; --i)
    {
      if (sb[i] == '\r') sb.Remove(i, 1);
    }

    text = sb.ToString();
    
    while (text.Contains("  "))
    {
      text = text.Replace("  ", " ");
    }

    return text.Replace("\n ", "\n").Replace(" \n", "\n");
  }

  public override void VisitParam(XmlElement element)
  {
    ProcessParam(element, "name", ourParamFactory);
  }

  private void ProcessParam(XmlElement element, string nameAttrName, Func<string, IParamContentSegment> factory)
  {
    myVisitedNodes.Add(element);
    var paramName = element.GetAttribute(nameAttrName);
    if (paramName == string.Empty)
    {
      paramName = UndefinedParam;
    }

    var paramSegment = factory.Invoke(paramName);
    var metadata = new ContentSegmentsMetadata(paramSegment, paramSegment.ContentSegments);
    using (new WithPushedToStackContentSegments(myContentSegmentsStack, metadata, ourLogger))
    {
      ExecuteActionOverChildren(element, Visit);
    }
      
    ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(paramSegment));
  }
    
  private void ExecuteWithTopmostContentSegments([NotNull] Action<ContentSegmentsMetadata> action)
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

    var processedText = PreprocessText(text.Value);
    var highlighter = myHighlightersProvider.TryGetReSharperHighlighter(myDocCommentAttributeId, processedText.Length);
    var textContentSegment = new MergeableTextContentSegment(new HighlightedText(processedText, highlighter));
    ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(textContentSegment));
  }

  public override void VisitPara(XmlElement element)
  {
    var paragraphContentSegment = new ParagraphContentSegment(ContentSegments.CreateEmpty());
    ProcessEntityWithContentSegments(paragraphContentSegment, element);
  }

  private void ProcessEntityWithContentSegments(
    [NotNull] IEntityWithContentSegments entityWithContentSegments, 
    [NotNull] XmlElement element,
    bool addToTopmostSegments = true)
  {
    var metadata = new ContentSegmentsMetadata(entityWithContentSegments, entityWithContentSegments.ContentSegments);
    using (new WithPushedToStackContentSegments(myContentSegmentsStack, metadata, ourLogger))
    {
      ExecuteActionOverChildren(element, Visit);
    }

    if (addToTopmostSegments)
    {
      ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(entityWithContentSegments));
    }
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

    var reference = CreateCodeEntityReference(exceptionName);

    exceptionName = reference.Resolve(myResolveContext).DeclaredElement switch
    {
      { } declaredElement => Present(declaredElement),
      null => BeautifyCodeEntityId(exceptionName)
    };

    var highlighter = myHighlightersProvider.GetReSharperExceptionHighlighter(
      0, exceptionName.Length, reference, myResolveContext);
    
    var exceptionSegment = new ExceptionContentSegment(new HighlightedText(exceptionName, highlighter));
    ProcessEntityWithContentSegments(exceptionSegment, element);
  }

  private static string Present(IDeclaredElement element)
  {
    return DeclaredElementPresenter.Format(
      CSharpLanguage.Instance, XmlDocPresenterUtil.LinkedElementPresentationStyle, element).Text;
  }
  
  private static string BeautifyCodeEntityId(string id)
  {
    return id[(id.IndexOf(":", StringComparison.Ordinal) + 1)..];
  }

  public override void VisitParamRef(XmlElement element)
  {
    ProcessArbitraryParamRef(element, "name", length => myHighlightersProvider.GetParamRefElementHighlighter(0, length));
  }

  private void ProcessArbitraryParamRef(
    [NotNull] XmlElement element,
    [NotNull] string nameAttrName, 
    [NotNull] Func<int, TextHighlighter> highlighterFactory)
  {
    myVisitedNodes.Add(element);
    var paramName = element.GetAttribute(nameAttrName);
    if (paramName == string.Empty) paramName = UndefinedParam;
    paramName = PreprocessText(paramName);

    var highlighter = highlighterFactory.Invoke(paramName.Length);
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
      description ??= referenceRawText;
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
    var innerText = ElementHasOneTextChild(element, out var text) ? text : null;

    var seeAlso = factory.Invoke(attributeValue, innerText);
    if (IsTopmostContext())
    {
      ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(seeAlso)); 
    }
    else
    {
      AddHighlightedText(seeAlso.HighlightedText.Text, seeAlso.HighlightedText.Highlighters.First());
    }
  }

  private bool IsTopmostContext() => myContentSegmentsStack.Count == 0 || 
                                     myContentSegmentsStack.Peek().CorrespondingEntity is null;

  private void VisitSeeAlsoMember(XmlElement element)
  {
    ProcessSeeAlso(element, CRef, (referenceRawText, description) =>
    {
      var reference = CreateCodeEntityReference(referenceRawText);
      description = description switch
      {
        null when reference.Resolve(myResolveContext).DeclaredElement is { } declaredElement => Present(declaredElement),
        null => BeautifyCodeEntityId(referenceRawText),
        _ => description
      };

      var highlighter = myHighlightersProvider.GetSeeAlsoReSharperMemberHighlighter(
        0, description.Length, reference, myResolveContext);
      
      var highlightedText = new HighlightedText(description, new[] { highlighter });
      return new SeeAlsoMemberContentSegment(highlightedText, CreateCodeEntityReference(referenceRawText));
    });
  }
  
  private XmlDocCodeEntityReference CreateCodeEntityReference(string rawValue)
  {
    return new XmlDocCodeEntityReference(rawValue, myPsiServices, myPsiModule);
  }

  public override void VisitTypeParam(XmlElement element)
  {
    ProcessParam(element, "name", ourTypeParamFactory);
  }

  public override void VisitTypeParamRef(XmlElement element)
  {
    ProcessArbitraryParamRef(element, "name", length => myHighlightersProvider.GetParamRefElementHighlighter(0, length));
  }

  public override void VisitExample(XmlElement element)
  {
    myVisitedNodes.Add(element);
    var exampleSegment = new ExampleContentSegment(ContentSegments.CreateEmpty());
    ProcessEntityWithContentSegments(exampleSegment, element);
  }

  public override void VisitSee(XmlElement element)
  {
    myVisitedNodes.Add(element);
    if (IsTopmostContext())
    {
      VisitSeeAlso(element);
      return;
    }

    IReference reference = null;
    if (element.GetAttribute(CRef) is { } cRefAttrValue && !cRefAttrValue.IsEmpty())
    {
      reference = CreateCodeEntityReference(cRefAttrValue);
    }
    else if (element.GetAttribute(Href) is { } hrefAttrValue && !hrefAttrValue.IsEmpty())
    {
      reference = new HttpReference(hrefAttrValue);
    }
    else if (element.GetAttribute(LangWord) is { } langWordAttrValue && !langWordAttrValue.IsEmpty())
    {
      reference = new LangWordReference(langWordAttrValue);
    }

    if (reference is null) return;
    var content = BeautifyCodeEntityId(reference.RawValue);
    
    if (reference is ICodeEntityReference codeEntityReference &&
        codeEntityReference.Resolve(myResolveContext).DeclaredElement is { } declaredElement)
    {
      content = Present(declaredElement);
    }
    
    if (ElementHasOneTextChild(element, out var text))
    {
      content = PreprocessText(text);
    }
    
    ProcessSee(content, reference);
  }

  private void ProcessSee([NotNull] string content, [NotNull] IReference reference)
  {
    var highlighter = reference switch
    {
      ICodeEntityReference codeEntityReference => 
        myHighlightersProvider.GetReSharperSeeCodeEntityHighlighter(0, content.Length, codeEntityReference, myResolveContext),
      IHttpReference => myHighlightersProvider.GetSeeHttpLinkHighlighter(0, content.Length),
      ILangWordReference => myHighlightersProvider.GetSeeLangWordHighlighter(0, content.Length),
      _ => throw new ArgumentOutOfRangeException(reference.GetType().Name)
    };
    
    AddHighlightedText(content, highlighter);
  }

  public override void VisitList(XmlElement element)
  {
    myVisitedNodes.Add(element);
    var typeOfList = element.GetAttribute("type");

    switch (typeOfList)
    {
      case "number" or "bullet":
        ProcessList(element, typeOfList);
        break;
      case "table":
        ProcessTable(element);
        break;
    }
  }

  private void ProcessList([NotNull] XmlElement element, string typeOfList)
  {
    ListKind? listKindNullable = typeOfList switch
    {
      "number" => ListKind.Number,
      "bullet" => ListKind.Bullet,
      _ => null
    };

    if (listKindNullable is not { } listKind) return;
    
    var list = new ListSegment(listKind);
    ExecuteActionOverTermsAndDescriptions(element, (termSegments, descriptionSegments) =>
    {
      var listItem = new ListItemImpl(termSegments, descriptionSegments);
      list.Items.Add(listItem);
    });
    
    ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(list));
  }

  private void ExecuteActionOverTermsAndDescriptions(
    [NotNull] XmlElement element, 
    [NotNull] Action<IEntityWithContentSegments, IEntityWithContentSegments> actionWithTermAndDescription)
  {
    foreach (var child in element.ChildNodes)
    {
      if (child is not XmlElement childElement) continue;
      
      var term = childElement.GetElementsByTagName("term").Item(0) as XmlElement;
      var description = childElement.GetElementsByTagName("description").Item(0) as XmlElement;

      IEntityWithContentSegments termSegments = null;
      if (term is { })
      {
        myVisitedNodes.Add(term);
        termSegments = new EntityWithContentSegments(ContentSegments.CreateEmpty());
        ProcessEntityWithContentSegments(termSegments, term, false);
      }

      IEntityWithContentSegments descriptionSegments = null;
      if (description is { })
      {
        myVisitedNodes.Add(description);
        descriptionSegments = new EntityWithContentSegments(ContentSegments.CreateEmpty());
        ProcessEntityWithContentSegments(descriptionSegments, description, false);
      }
      
      actionWithTermAndDescription(termSegments, descriptionSegments);
    }
  }

  private void ProcessTable([NotNull] XmlElement element)
  {
    var table = new TableSegment(null);
    ExecuteActionOverTermsAndDescriptions(element, (termSegments, descriptionSegments) =>
    {
      var termCell = new TableCell(termSegments.ContentSegments, TableCellProperties.DefaultProperties);
      var descriptionCell = new TableCell(descriptionSegments.ContentSegments, TableCellProperties.DefaultProperties);
      var row = new TableSegmentRow();
      row.Cells.Add(termCell);
      row.Cells.Add(descriptionCell);
      table.Rows.Add(row);
    });
    
    ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(table));
  }
  
  public override void VisitCode(XmlElement element)
  {
    myVisitedNodes.Add(element);
    if (ElementHasOneTextChild(element, out var text))
    {
      var codeFragment = CreateCodeFragment(text);
      var codeSegment = new CodeSegment(codeFragment.PreliminaryText, codeFragment.HighlightingRequestId);
      ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(codeSegment));
    }
  }

  private record CodeFragment(IHighlightedText PreliminaryText, int HighlightingRequestId);
  
  [NotNull]
  private CodeFragment CreateCodeFragment(string text)
  {
    CodeFragment CreateDefaultFragment() => new(new HighlightedText(text), 0);
    
    if (myComment.GetContainingFile() is not { } file) return CreateDefaultFragment();
    
    var requestBuilder = myLanguageManager.GetService<ICodeHighlightingRequestBuilder>(file.Language);

    if (requestBuilder.CreateNodeFromText(text, myComment) is { } node)
    {
      var codeHighlightingRequest = requestBuilder.CreateRequest(file, myComment, node);
      var id = myCodeFragmentHighlightingManager.AddRequestForHighlighting(codeHighlightingRequest);
      
      var preliminaryHighlighter = myLanguageManager.GetService<IPreliminaryCodeHighlighter>(file.Language);
      var highlightedText = HighlightedText.CreateEmptyText();
      var context = new CodeHighlightingContext(highlightedText, new UserDataHolder());
      
      node.ProcessThisAndDescendants(preliminaryHighlighter, context);

      return new CodeFragment(highlightedText, id); 
    }

    return CreateDefaultFragment();
  }
}