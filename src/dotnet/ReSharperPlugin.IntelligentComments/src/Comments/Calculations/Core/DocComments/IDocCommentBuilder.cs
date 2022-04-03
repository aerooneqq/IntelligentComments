using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Modules;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Util;
using JetBrains.Rider.Model;
using JetBrains.Util;
using JetBrains.Util.Logging;
using ReSharperPlugin.IntelligentComments.Comments.Caches;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;
using ReSharperPlugin.IntelligentComments.Comments.CodeFragmentsHighlighting;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

public interface IDocCommentBuilder
{
  [CanBeNull] IDocComment Build();
}

public abstract class DocCommentBuilderBase : XmlDocVisitorWitCustomElements, IDocCommentBuilder
{
  [NotNull] private const string Name = "name";
  [NotNull] private const string UndefinedParam = "???";
  [NotNull] private const string Href = "href";
  [NotNull] private const string LangWord = "langword";
  

  [NotNull] private static readonly ILogger ourLogger = Logger.GetLogger<DocCommentBuilderBase>();
  [NotNull] private static readonly Func<IHighlightedText, IParamContentSegment> ourParamFactory = name => new ParamContentSegment(name);
  [NotNull] private static readonly Func<IHighlightedText, ITypeParamSegment> ourTypeParamFactory = name => new TypeParamSegment(name);
  
  [NotNull] private readonly Stack<ContentSegmentsMetadata> myContentSegmentsStack;
  [NotNull] private readonly IHighlightersProvider myHighlightersProvider;
  [NotNull] private readonly IPsiServices myPsiServices;
  [NotNull] private readonly IPsiModule myPsiModule;
  [NotNull] private readonly CodeFragmentHighlightingManager myCodeFragmentHighlightingManager;
  [NotNull] private readonly ILanguageManager myLanguageManager;
  [NotNull] private readonly IDomainResolveContext myDomainResolveContext;
  [NotNull] private readonly string myDocCommentAttributeId;
  [NotNull] private readonly string myParamAttributeId;
  [NotNull] private readonly string myTypeParamAttributeId;
  [NotNull] private readonly ReferencesCache myReferencesCache;


  protected DocCommentBuilderBase([NotNull] IDocCommentBlock comment) : base(comment)
  {
    myDomainResolveContext = new DomainResolveContextImpl(comment.GetSolution(), comment.GetSourceFile()?.Document);
    myLanguageManager = LanguageManager.Instance;
    myHighlightersProvider = myLanguageManager.GetService<IHighlightersProvider>(comment.Language);
    myContentSegmentsStack = new Stack<ContentSegmentsMetadata>();
    myCodeFragmentHighlightingManager = comment.GetSolution().GetComponent<CodeFragmentHighlightingManager>();
    myReferencesCache = comment.GetSolution().GetComponent<ReferencesCache>();
    myPsiServices = comment.GetPsiServices();
    myPsiModule = comment.GetPsiModule();
    myDocCommentAttributeId = DefaultLanguageAttributeIds.DOC_COMMENT;
    myParamAttributeId = DefaultLanguageAttributeIds.PARAMETER;
    myTypeParamAttributeId = DefaultLanguageAttributeIds.TYPE_PARAMETER;
  }
  

  public IDocComment Build()
  {
    try
    {
      if (CommentsBuilderUtil.TryGetAdjustedComment(InitialComment) is not { } commentBlock) return null;
      
      AdjustedComment = commentBlock;
      return ProcessAdjustedComment();
    }
    catch (Exception ex)
    {
      ourLogger.LogException(ex);
      return null;
    }
  }
  
  [CanBeNull]
  private DocComment ProcessAdjustedComment()
  {
    if (CommentsBuilderUtil.TryGetXml(AdjustedComment) is not { } xmlNode) return null;
    
    var topmostContentSegments = ContentSegmentsMetadata.CreateEmpty();
    using (new WithPushedToStackContentSegments(myContentSegmentsStack, topmostContentSegments, ourLogger))
    {
      Visit(xmlNode);
    }
      
    var content = new IntelligentCommentContent(topmostContentSegments.ContentSegments);
    return new DocComment(content, InitialComment.GetDocumentRange());
  }
  
  public override void Visit(XmlNode node)
  {
    if (VisitedNodes.Contains(node)) return;
    base.Visit(node);
  }
  
  protected override void VisitImage(XmlElement element)
  {
    if (element.GetAttributeNode(CommentsBuilderUtil.ImageSourceAttrName) is not { } sourceAttribute) return;
    var path = FileSystemPath.TryParse(sourceAttribute.Value);
    if (path == FileSystemPath.Empty) return;

    var reference = new FileDomainReference(path);
    IHighlightedText description = HighlightedText.EmptyText;
    if (CommentsBuilderUtil.ElementHasOneTextChild(element, out var text))
    {
      description = new HighlightedText(text);
    }
    
    var imageSegment = new ImageContentSegment(reference, description);
    ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(imageSegment));
  }

  protected override void VisitInvariant(XmlElement element)
  {
    var solution = myDomainResolveContext.Solution;
    if (!IsTopmostContext() ||
        TryBuildInvariantContentSegment(element, solution, myHighlightersProvider, true) is not { } invariant)
    {
      return;
    }
    
    ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(invariant));
  }

  [CanBeNull]
  public static IInvariantContentSegment TryBuildInvariantContentSegment(
    [NotNull] XmlElement element, 
    [NotNull] ISolution solution,
    [NotNull] IHighlightersProvider highlightersProvider,
    bool checkValidity)
  {
    const string attributeName = CommentsBuilderUtil.InvariantNameAttrName;
    
    IDomainReference CreateReference([NotNull] string name) => new InvariantDomainReference(name);
    bool IsReferenceValid(IDomainReference reference) => checkValidity && CheckInvariantReferenceIsValid(reference, solution);

    var tagInfo = CommentsBuilderUtil.TryExtractTagInfo(
      element, attributeName, highlightersProvider, CreateReference, IsReferenceValid);
    
    if (tagInfo is not var (nameText, descriptionText)) return null;

    var segments = new List<IContentSegment>();
    if (!descriptionText.Text.IsNullOrWhitespace())
    {
      segments.Add(new TextContentSegment(descriptionText));
    }

    var content = new EntityWithContentSegments(new ContentSegments(segments));
    return new InvariantContentSegment(nameText, content);
  }

  private static bool CheckInvariantReferenceIsValid([NotNull] IDomainReference domainReference, [NotNull] ISolution solution)
  {
    if (domainReference is not IInvariantDomainReference invariantReference) return false;

    var cache = solution.GetComponent<InvariantsNamesCache>();
    return cache.GetInvariantNameCount(invariantReference.InvariantName) == 1;
  }
  
  protected override void VisitReference(XmlElement element)
  {
    if (!IsTopmostContext()) return;
    
    const string attributeName = CommentsBuilderUtil.InvariantReferenceSourceAttrName;
    
    IDomainReference CreateReference([NotNull] string name) => new InvariantDomainReference(name);
    bool IsReferenceValid(IDomainReference reference) => CheckInvariantReferenceIsValid(reference, myDomainResolveContext.Solution);
    
    var tagInfo = CommentsBuilderUtil.TryExtractTagInfo(
      element, attributeName, myHighlightersProvider, CreateReference, IsReferenceValid);
    
    if (tagInfo is not var (nameText, descriptionText)) return;

    var reference = new InvariantDomainReference(nameText.Text);
    var segments = new ContentSegments(new List<IContentSegment> { new TextContentSegment(descriptionText) });
    var content = new EntityWithContentSegments(segments);
    var referenceSegment = new ReferenceContentSegment(reference, nameText, content);
    ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(referenceSegment));
  }

  public override void VisitSummary(XmlElement element)
  {
    VisitedNodes.Add(element);
    var summary = new SummaryContentSegment(ContentSegments.CreateEmpty());
    ProcessEntityWithContentSegments(summary, element);
  }
    
  private static void ExecuteActionOverChildren([NotNull] XmlElement parent, [NotNull] Action<XmlNode> actionWithNode)
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
    VisitedNodes.Add(element);
    if (CommentsBuilderUtil.ElementHasOneTextChild(element, out var value))
    {
      (value, var length) = PreprocessTextWithContext(value, element);
      var highlighter = myHighlightersProvider.GetCXmlElementHighlighter(0, length);
      AddHighlightedText(value, highlighter);
      
      return;
    }
      
    ExecuteActionOverChildren(element, Visit);
  }
  
  private static TextProcessingResult PreprocessTextWithContext([NotNull] string text, [NotNull] XmlNode context)
  {
    return CommentsBuilderUtil.PreprocessTextWithContext(text, context);
  }

  private void AddHighlightedText([NotNull] string text, [NotNull] TextHighlighter highlighter)
  {
    var highlightedText = new HighlightedText(text, new[] { highlighter });
    var textContentSegment = new MergeableTextContentSegment(highlightedText);
    ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(textContentSegment));
  }
  
  public override void VisitParam(XmlElement element)
  {
    ProcessParam(element, Name, ourParamFactory);
  }

  private void ProcessParam(
    [NotNull] XmlElement element, 
    [NotNull] string nameAttrName, 
    [NotNull] Func<IHighlightedText, IParamContentSegment> factory)
  {
    VisitedNodes.Add(element);
    var paramName = element.GetAttribute(nameAttrName);
    if (paramName == string.Empty)
    {
      paramName = UndefinedParam;
    }

    var highlighter = myHighlightersProvider.TryGetReSharperHighlighter(myParamAttributeId, paramName.Length);
    var paramSegment = factory.Invoke(new HighlightedText(paramName, highlighter));
    
    var metadata = new ContentSegmentsMetadata(paramSegment, paramSegment.ContentSegments);
    using (new WithPushedToStackContentSegments(myContentSegmentsStack, metadata, ourLogger))
    {
      ExecuteActionOverChildren(element, Visit);
    }
      
    ExecuteWithTopmostContentSegments(segmentsMetadata => segmentsMetadata.ContentSegments.Segments.Add(paramSegment));
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
    VisitedNodes.Add(text);

    var (processedText, length) = PreprocessTextWithContext(text.Value, text);
    var highlighter = myHighlightersProvider.TryGetReSharperHighlighter(myDocCommentAttributeId, length);
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
      ExecuteWithTopmostContentSegments(
        segmentsMetadata => segmentsMetadata.ContentSegments.Segments.Add(entityWithContentSegments));
    }
  }

  public override void VisitReturns(XmlElement element)
  {
    VisitedNodes.Add(element);
    var returnSegment = new ReturnContentSegment(ContentSegments.CreateEmpty());
    ProcessEntityWithContentSegments(returnSegment, element);
  }

  public override void VisitRemarks(XmlElement element)
  {
    VisitedNodes.Add(element);
    var remarksSegment = new RemarksContentSegment(ContentSegments.CreateEmpty());
    ProcessEntityWithContentSegments(remarksSegment, element);
  }

  public override void VisitValue(XmlElement element)
  {
    VisitedNodes.Add(element);
    var valueSegment = new ValueSegment(ContentSegments.CreateEmpty());
    ProcessEntityWithContentSegments(valueSegment, element);
  }

  public override void VisitException(XmlElement element)
  {
    VisitedNodes.Add(element);
    var exceptionName = UndefinedParam;
    if (element.GetAttributeNode(CommentsBuilderUtil.CRef) is { } attribute)
    {
      VisitedNodes.Add(attribute);
      exceptionName = attribute.Value;
    }

    var reference = CreateCodeEntityReference(exceptionName);

    exceptionName = (reference.Resolve(myDomainResolveContext) as DeclaredElementDomainResolveResult)?.DeclaredElement switch
    {
      { } declaredElement => Present(declaredElement),
      null => BeautifyCodeEntityId(exceptionName)
    };

    var highlighter = myHighlightersProvider.GetReSharperExceptionHighlighter(
      0, exceptionName.Length, reference, myDomainResolveContext);
    
    var exceptionSegment = new ExceptionContentSegment(new HighlightedText(exceptionName, highlighter));
    ProcessEntityWithContentSegments(exceptionSegment, element);
  }

  private string Present([NotNull] IDeclaredElement element)
  {
    return DeclaredElementPresenter.Format(
      AdjustedComment.Language, XmlDocPresenterUtil.LinkedElementPresentationStyle, element).Text;
  }
  
  private static string BeautifyCodeEntityId([NotNull] string id)
  {
    return id[(id.IndexOf(":", StringComparison.Ordinal) + 1)..];
  }

  public override void VisitParamRef(XmlElement element)
  {
    ProcessArbitraryParamRef(
      element, Name, length => myHighlightersProvider.TryGetReSharperHighlighter(myParamAttributeId, length));
  }

  private void ProcessArbitraryParamRef(
    [NotNull] XmlElement element,
    [NotNull] string nameAttrName, 
    [NotNull] Func<int, TextHighlighter> highlighterFactory)
  {
    VisitedNodes.Add(element);
    var paramName = element.GetAttribute(nameAttrName);
    if (paramName == string.Empty) paramName = UndefinedParam;
    (paramName, var length) = PreprocessTextWithContext(paramName, element);

    var highlighter = highlighterFactory.Invoke(length);
    AddHighlightedText(paramName, highlighter);
  }

  public override void VisitSeeAlso(XmlElement element)
  {
    VisitedNodes.Add(element);
    var href = element.GetAttribute(Href);
    if (href != string.Empty)
    {
      VisitSeeAlsoLink(element);
      return;
    }
    
    VisitSeeAlsoMember(element);
  }

  private void VisitSeeAlsoLink([NotNull] XmlElement element)
  {
    ProcessSeeAlso(element, Href, (referenceRawText, description) =>
    {
      description ??= referenceRawText;
      
      (description, var length) = PreprocessTextWithContext(description, element);
      
      var highlighter = myHighlightersProvider.GetSeeAlsoLinkHighlighter(0, length);
      var highlightedText = new HighlightedText(description, new[] { highlighter });
      return new SeeAlsoLinkContentSegment(highlightedText, new HttpDomainReference(referenceRawText));
    });
  }

  private void ProcessSeeAlso(
    [NotNull] XmlElement element,
    [NotNull] string attributeName,
    [NotNull] Func<string, string, ISeeAlsoContentSegment> factory)
  {
    var attributeValue = element.GetAttribute(attributeName);
    var innerText = CommentsBuilderUtil.ElementHasOneTextChild(element, out var text) ? text : null;

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

  private void VisitSeeAlsoMember([NotNull] XmlElement element)
  {
    ProcessSeeAlso(element, CommentsBuilderUtil.CRef, (referenceRawText, description) =>
    {
      var reference = CreateCodeEntityReference(referenceRawText);
      
      var resolveResult = reference.Resolve(myDomainResolveContext) as DeclaredElementDomainResolveResult;
      var declaredElement = resolveResult?.DeclaredElement;
      
      description = description switch
      {
        null when declaredElement is { } => Present(declaredElement),
        null => BeautifyCodeEntityId(referenceRawText),
        _ => description
      };

      (description, var length) = PreprocessTextWithContext(description, element);

      var highlighter = myHighlightersProvider.GetSeeAlsoReSharperMemberHighlighter(0, length, reference, myDomainResolveContext);
      
      var highlightedText = new HighlightedText(description, new[] { highlighter });
      return new SeeAlsoMemberContentSegment(highlightedText, reference);
    });
  }
  
  private IDomainReference CreateCodeEntityReference([NotNull] string rawValue)
  {
    var realReference = new XmlDocCodeEntityDomainReference(rawValue, myPsiServices, myPsiModule);
    var referenceId = myReferencesCache.AddReferenceIfNotPresent(myDomainResolveContext.Document, realReference);
    return new ProxyDomainReference(referenceId);
  }

  public override void VisitTypeParam(XmlElement element)
  {
    ProcessParam(element, Name, ourTypeParamFactory);
  }

  public override void VisitTypeParamRef(XmlElement element)
  {
    ProcessArbitraryParamRef(
      element, Name, length => myHighlightersProvider.TryGetReSharperHighlighter(myTypeParamAttributeId, length));
  }

  public override void VisitExample(XmlElement element)
  {
    VisitedNodes.Add(element);
    var exampleSegment = new ExampleContentSegment(ContentSegments.CreateEmpty());
    ProcessEntityWithContentSegments(exampleSegment, element);
  }

  public override void VisitSee(XmlElement element)
  {
    VisitedNodes.Add(element);
    if (IsTopmostContext())
    {
      VisitSeeAlso(element);
      return;
    }

    IDomainReference domainReference = null;
    if (element.GetAttribute(CommentsBuilderUtil.CRef) is { } cRefAttrValue && !cRefAttrValue.IsEmpty())
    {
      domainReference = CreateCodeEntityReference(cRefAttrValue);
    }
    else if (element.GetAttribute(Href) is { } hrefAttrValue && !hrefAttrValue.IsEmpty())
    {
      domainReference = new HttpDomainReference(hrefAttrValue);
    }
    else if (element.GetAttribute(LangWord) is { } langWordAttrValue && !langWordAttrValue.IsEmpty())
    {
      domainReference = new LangWordDomainReference(langWordAttrValue);
    }

    if (domainReference is null) return;
    var content = BeautifyCodeEntityId(domainReference.RawValue);

    if (domainReference.Resolve(myDomainResolveContext) is DeclaredElementDomainResolveResult { DeclaredElement: { } declaredElement })
    {
      content = Present(declaredElement);
    }
    
    if (CommentsBuilderUtil.ElementHasOneTextChild(element, out var text))
    {
      content = text;
    }
    
    ProcessSee(content, domainReference, element);
  }

  private void ProcessSee([NotNull] string content, [NotNull] IDomainReference domainReference, [NotNull] XmlElement element)
  {
    (content, var length) = PreprocessTextWithContext(content, element);
    
    var highlighter = domainReference switch
    {
      ICodeEntityDomainReference or IProxyDomainReference => 
        myHighlightersProvider.GetReSharperSeeCodeEntityHighlighter(0, length, domainReference, myDomainResolveContext),
      IHttpDomainReference => myHighlightersProvider.GetSeeHttpLinkHighlighter(0, length),
      ILangWordDomainReference => myHighlightersProvider.GetSeeLangWordHighlighter(0, length),
      _ => throw new ArgumentOutOfRangeException(domainReference.GetType().Name)
    };
    
    AddHighlightedText(content, highlighter);
  }

  public override void VisitList(XmlElement element)
  {
    VisitedNodes.Add(element);
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

  private void ProcessList([NotNull] XmlElement element, [NotNull] string typeOfList)
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
        VisitedNodes.Add(term);
        termSegments = new EntityWithContentSegments(ContentSegments.CreateEmpty());
        ProcessEntityWithContentSegments(termSegments, term, false);
      }

      IEntityWithContentSegments descriptionSegments = null;
      if (description is { })
      {
        VisitedNodes.Add(description);
        descriptionSegments = new EntityWithContentSegments(ContentSegments.CreateEmpty());
        ProcessEntityWithContentSegments(descriptionSegments, description, false);
      }
      
      actionWithTermAndDescription(termSegments, descriptionSegments);
    }
  }

  private void ProcessTable([NotNull] XmlElement element)
  {
    var table = new TableSegment(null);
    ExecuteActionOverTermsAndDescriptions(element, (term, description) =>
    {
      var termContentSegments = term?.ContentSegments ?? ContentSegments.CreateEmpty();
      var descriptionSegments = description?.ContentSegments ?? ContentSegments.CreateEmpty();
      var termCell = new TableCell(termContentSegments, TableCellProperties.DefaultProperties);
      var descriptionCell = new TableCell(descriptionSegments, TableCellProperties.DefaultProperties);
      var row = new TableSegmentRow();
      row.Cells.Add(termCell);
      row.Cells.Add(descriptionCell);
      table.Rows.Add(row);
    });
    
    ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(table));
  }
  
  public override void VisitCode(XmlElement element)
  {
    VisitedNodes.Add(element);
    if (CommentsBuilderUtil.ElementHasOneTextChild(element, out var text))
    {
      if (CanInlineCode(text))
      {
        VisitC(element);
        return;
      }
      
      var codeFragment = CreateCodeFragment(text);
      var codeSegment = new CodeSegment(codeFragment.PreliminaryText, codeFragment.HighlightingRequestId);
      ExecuteWithTopmostContentSegments(metadata => metadata.ContentSegments.Segments.Add(codeSegment));
    }
  }
  
  private static bool CanInlineCode([NotNull] string rawCodeText)
  {
    return !rawCodeText.Contains("\n");
  }

  private record CodeFragment(IHighlightedText PreliminaryText, int HighlightingRequestId);
  
  [NotNull]
  private CodeFragment CreateCodeFragment([NotNull] string text)
  {
    CodeFragment CreateDefaultFragment() => new(new HighlightedText(text), 0);
    
    if (AdjustedComment.GetContainingFile() is not { } file) return CreateDefaultFragment();
    
    var requestBuilder = myLanguageManager.GetService<ICodeHighlightingRequestBuilder>(file.Language);

    if (requestBuilder.CreateNodeOperations(text, AdjustedComment) is var (node, operations) &&
        operations.CreateTextForSandBox() is { } code &&
        file.GetSourceFile()?.Document is { } document)
    {
      var request = new CodeHighlightingRequest(file.Language, code, document, operations);
      var id = myCodeFragmentHighlightingManager.AddRequestForHighlighting(request);
      
      var preliminaryHighlighter = myLanguageManager.GetService<IPreliminaryCodeHighlighter>(file.Language);
      var highlightedText = HighlightedText.CreateEmptyText();
      var context = new CodeHighlightingContext(highlightedText, new UserDataHolder());
      
      node.ProcessThisAndDescendants(preliminaryHighlighter, context);

      return new CodeFragment(highlightedText, id); 
    }

    return CreateDefaultFragment();
  }
}