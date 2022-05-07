using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml;
using IntelligentComments.Comments.Caches;
using IntelligentComments.Comments.Caches.Names;
using IntelligentComments.Comments.Calculations.CodeHighlighting;
using IntelligentComments.Comments.Calculations.Core.DocComments.Tickets;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Domain.Core;
using IntelligentComments.Comments.Domain.Core.Content;
using IntelligentComments.Comments.Domain.Core.References;
using IntelligentComments.Comments.Domain.Impl;
using IntelligentComments.Comments.Domain.Impl.Content;
using IntelligentComments.Comments.Domain.Impl.References;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Modules;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Util;
using JetBrains.Util;
using JetBrains.Util.Logging;

namespace IntelligentComments.Comments.Calculations.Core.DocComments;

public interface IDocCommentBuilder
{
  [CanBeNull] IContentSegment Build([NotNull] XmlElement element);
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

  [CanBeNull] private readonly ICodeFragmentHighlightingManager myCodeFragmentHighlightingManager;

  [NotNull] private readonly Stack<ContentSegmentsMetadata> myContentSegmentsStack;
  [NotNull] private readonly IHighlightersProvider myHighlightersProvider;
  [NotNull] private readonly IPsiServices myPsiServices;
  [NotNull] private readonly IPsiModule myPsiModule;
  [NotNull] private readonly ILanguageManager myLanguageManager;
  [NotNull] private readonly IDomainResolveContext myDomainResolveContext;
  [NotNull] private readonly string myDocCommentAttributeId;
  [NotNull] private readonly string myParamAttributeId;
  [NotNull] private readonly string myTypeParamAttributeId;
  [NotNull] private readonly ReferencesCache myReferencesCache;
  [NotNull] private readonly ISolution mySolution;


  protected DocCommentBuilderBase([NotNull] IDocCommentBlock comment) : base(comment)
  {
    mySolution = comment.GetSolution();
    myDomainResolveContext = new DomainResolveContextImpl(comment.GetSolution(), comment.GetSourceFile()?.Document);
    myLanguageManager = LanguageManager.Instance;
    myHighlightersProvider = myLanguageManager.GetService<IHighlightersProvider>(comment.Language);
    myContentSegmentsStack = new Stack<ContentSegmentsMetadata>();
    myCodeFragmentHighlightingManager = comment.GetSolution().TryGetComponent<ICodeFragmentHighlightingManager>();
    myReferencesCache = comment.GetSolution().GetComponent<ReferencesCache>();
    myPsiServices = comment.GetPsiServices();
    myPsiModule = comment.GetPsiModule();
    myDocCommentAttributeId = DefaultLanguageAttributeIds.DOC_COMMENT;
    myParamAttributeId = DefaultLanguageAttributeIds.PARAMETER;
    myTypeParamAttributeId = DefaultLanguageAttributeIds.TYPE_PARAMETER;
  }


  public IContentSegment Build(XmlElement element)
  {
    if (DocCommentsBuilderUtil.TryGetAdjustedComment(InitialComment) is not { } commentBlock) return null;
    AdjustedComment = commentBlock;
    
    var topmostContentSegments = ContentSegmentsMetadata.CreateEmpty();
    using (new WithPushedToStackContentSegments(myContentSegmentsStack, topmostContentSegments, ourLogger))
    {
      Visit(element);
    }

    return topmostContentSegments.ContentSegments.Segments.FirstOrDefault();
  }
  
  public IDocComment Build()
  {
    try
    {
      if (DocCommentsBuilderUtil.TryGetAdjustedComment(InitialComment) is not { } commentBlock) return null;
      
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
    if (DocCommentsBuilderUtil.TryGetXml(AdjustedComment) is not { } xmlNode) return null;
    
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
    if (element.GetAttributeNode(DocCommentsBuilderUtil.ImageSourceAttrName) is not { } sourceAttribute) return;
    var path = FileSystemPath.TryParse(sourceAttribute.Value);
    if (path == FileSystemPath.Empty) return;

    var reference = new FileDomainReference(path);
    IHighlightedText description = HighlightedText.EmptyText;
    if (DocCommentsBuilderUtil.ElementHasOneTextChild(element, out var text))
    {
      description = new HighlightedText(text);
    }
    
    var imageSegment = new ImageContentSegment(reference, description);
    AddToTopmostContentSegments(imageSegment);
  }

  protected override void VisitInvariant(XmlElement element)
  {
    if (!IsTopmostContext()) return;

    var solution = myDomainResolveContext.Solution;
    IDomainReference CreateInvariantNameReference([NotNull] string name) => new NamedEntityDomainReference(name, NameKind.Invariant);
    bool CheckReferenceValidity(IDomainReference reference) => CheckNamedEntityReferenceIsValid(reference, solution, NameKind.Invariant);
    const string attrName = DocCommentsBuilderUtil.InvariantNameAttrName;
    
    var tagInfo = DocCommentsBuilderUtil.TryExtractTagInfoFromInvariant(
      element, attrName, myHighlightersProvider, CreateInvariantNameReference, CheckReferenceValidity);

    if (BuildInvariantContentSegmentFrom(tagInfo) is not { } invariant) return;
    AddToTopmostContentSegments(invariant);
  }

  [CanBeNull]
  private static InvariantContentSegment BuildInvariantContentSegmentFrom(TagInfo? tagInfo)
  {
    if (tagInfo is not var (nameText, descriptionText)) return null;
    return new InvariantContentSegment(nameText, descriptionText);
  }
  
  private static bool CheckNamedEntityReferenceIsValid(
    [NotNull] IDomainReference domainReference, 
    [NotNull] ISolution solution,
    NameKind nameKind)
  {
    if (domainReference is not INamedEntityDomainReference invariantReference) return false;

    var cache = NamesCacheUtil.GetCacheFor(solution, nameKind);
    return cache.GetNameCount(invariantReference.Name) == 1;
  }

  protected override void VisitReference(XmlElement element)
  {
    if (!IsTopmostContext()) return;
    if (DocCommentsBuilderUtil.TryExtractOneReferenceNameKindFromReferenceTag(element) is not { } nameKind) return;
    
    IDomainReference CreateReference([NotNull] string name) => new NamedEntityDomainReference(name, nameKind);
    bool IsReferenceValid(IDomainReference reference) => CheckNamedEntityReferenceIsValid(reference, myDomainResolveContext.Solution, nameKind);
    
    var tagInfo = DocCommentsBuilderUtil.TryExtractTagInfoFromReference(
      element, nameKind, myHighlightersProvider, CreateReference, IsReferenceValid);
    
    if (tagInfo is not var (nameText, descriptionText) || nameText is null) return;

    var reference = new NamedEntityDomainReference(nameText.Text, nameKind);
    var segments = new ContentSegments(new List<IContentSegment> { new TextContentSegment(descriptionText) });
    var content = new EntityWithContentSegments(segments);
    var referenceSegment = new ReferenceContentSegment(reference, nameText, content);
    AddToTopmostContentSegments(referenceSegment);
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
    if (DocCommentsBuilderUtil.ElementHasOneTextChild(element, out var value))
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
    return DocCommentsBuilderUtil.PreprocessTextWithContext(text, context);
  }

  private void AddHighlightedText([NotNull] string text, [NotNull] TextHighlighter highlighter)
  {
    var highlightedText = new HighlightedText(text, new[] { highlighter });
    var textContentSegment = new MergeableTextContentSegment(highlightedText);
    AddToTopmostContentSegments(textContentSegment);
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
      
    AddToTopmostContentSegments(paramSegment);
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
  
  private void AddToTopmostContentSegments([NotNull] IContentSegment segment) =>
    ExecuteWithTopmostContentSegments(segmentsMetadata => segmentsMetadata.ContentSegments.Segments.Add(segment));

  public override void VisitText(XmlText text)
  {
    VisitedNodes.Add(text);

    var (processedText, length) = PreprocessTextWithContext(text.Value, text);
    var highlighter = myHighlightersProvider.TryGetReSharperHighlighter(myDocCommentAttributeId, length);
    var textContentSegment = new MergeableTextContentSegment(new HighlightedText(processedText, highlighter));
    AddToTopmostContentSegments(textContentSegment);
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
      AddToTopmostContentSegments(entityWithContentSegments);
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
    if (element.GetAttributeNode(DocCommentsBuilderUtil.CRef) is { } attribute)
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
      return new SeeAlsoLinkContentSegment(highlightedText, new HttpDomainReference(referenceRawText, referenceRawText));
    });
  }

  private void ProcessSeeAlso(
    [NotNull] XmlElement element,
    [NotNull] string attributeName,
    [NotNull] Func<string, string, ISeeAlsoContentSegment> factory)
  {
    var attributeValue = element.GetAttribute(attributeName);
    var innerText = DocCommentsBuilderUtil.ElementHasOneTextChild(element, out var text) ? text : null;

    var seeAlso = factory.Invoke(attributeValue, innerText);
    if (IsTopmostContext())
    {
      AddToTopmostContentSegments(seeAlso); 
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
    ProcessSeeAlso(element, DocCommentsBuilderUtil.CRef, (referenceRawText, description) =>
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
  
  [NotNull]
  private IDomainReference CreateCodeEntityReference([NotNull] string rawValue)
  {
    var realReference = new XmlDocCodeEntityDomainReference(rawValue, myPsiServices, myPsiModule);
    var referenceId = myReferencesCache.AddReferenceIfNotPresent(myDomainResolveContext.Document, realReference);
    return new ProxyDomainReference(referenceId, realReference.RawValue);
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
    if (element.GetAttribute(DocCommentsBuilderUtil.CRef) is { } cRefAttrValue && !cRefAttrValue.IsEmpty())
    {
      domainReference = CreateCodeEntityReference(cRefAttrValue);
    }
    else if (element.GetAttribute(Href) is { } hrefAttrValue && !hrefAttrValue.IsEmpty())
    {
      domainReference = new HttpDomainReference(hrefAttrValue, hrefAttrValue);
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
    
    if (DocCommentsBuilderUtil.ElementHasOneTextChild(element, out var text))
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
    
    AddToTopmostContentSegments(list);
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
    
    AddToTopmostContentSegments(table);
  }
  
  public override void VisitCode(XmlElement element)
  {
    VisitedNodes.Add(element);
    if (DocCommentsBuilderUtil.ElementHasOneTextChild(element, out var text))
    {
      if (CanInlineCode(text))
      {
        VisitC(element);
        return;
      }
      
      var codeFragment = CreateCodeFragment(text);
      var codeSegment = new CodeSegment(codeFragment.PreliminaryText, codeFragment.HighlightingRequestId);
      AddToTopmostContentSegments(codeSegment);
    }
  }
  
  private static bool CanInlineCode([NotNull] string rawCodeText)
  {
    return !rawCodeText.Contains("\n");
  }
  
  protected override void VisitHack(XmlElement element) =>
    VisitHackOrTodo(
      element, 
      NameKind.Hack,
      (length) => myHighlightersProvider.GetHackHighlighter(0, length),
      (text, segments) => new HackContentSegment(text, segments)
    );
  
  [CanBeNull]
  private IHighlightedText TryGetNameFromNamedEntity(NameKind nameKind, [NotNull] XmlElement element)
  {
    IDomainReference CreateReference([NotNull] string name) => new NamedEntityDomainReference(name, nameKind);
    bool IsReferenceValid(IDomainReference reference) => CheckNamedEntityReferenceIsValid(reference, myDomainResolveContext.Solution, nameKind);
    
    return DocCommentsBuilderUtil.TryExtractNameAttributeFromNamedEntity(element, myHighlightersProvider, CreateReference, IsReferenceValid);
  }

  [CanBeNull]
  private IEntityWithContentSegments TryFillDescriptionAndTickets(
    [NotNull] XmlElement element,
    [NotNull] Func<int, TextHighlighter> highlighterFactory)
  {
    var content = new EntityWithContentSegments(ContentSegments.CreateEmpty());

    if (!FillDescriptionIfPresentTo(element, content, highlighterFactory)) return null;
    FillTicketsIfPresentTo(element, content);

    return content;
  }

  protected override void VisitTodo(XmlElement element) =>
    VisitHackOrTodo(
      element, 
      NameKind.Todo,
      (length) => myHighlightersProvider.GetToDoHighlighter(0, length),
      (text, segments) => new ToDoContentSegment(text, segments)
    );

  private void VisitHackOrTodo(
    [NotNull] XmlElement element, 
    NameKind nameKind,
    [NotNull] Func<int, TextHighlighter> highlighterCreator,
    [NotNull] Func<IHighlightedText, IEntityWithContentSegments, IContentSegment> segmentCreator)
  {
    if (!IsTopmostContext()) return;

    if (TryFillDescriptionAndTickets(element, highlighterCreator) is not { } entity) return;
    
    var nameText = TryGetNameFromNamedEntity(nameKind, element);
    var todoContentSegment = segmentCreator(nameText, entity);
    AddToTopmostContentSegments(todoContentSegment);
  }

  private bool FillDescriptionIfPresentTo(
    [NotNull] XmlElement parent, 
    [NotNull] EntityWithContentSegments entity,
    [NotNull] Func<int, TextHighlighter> highlighterFactory)
  {
    var descriptionSection = parent.GetChildElements().FirstOrDefault(child => child.Name == DocCommentsBuilderUtil.DescriptionTagName);
    if (descriptionSection is null) return false;
    ProcessEntityWithContentSegments(entity, descriptionSection, addToTopmostSegments: false);
    foreach (var segment in entity.ContentSegments.Segments)
    {
      if (segment is ITextContentSegment textContentSegment)
      {
        var length = textContentSegment.Text.Text.Length;
        var newHighlighters = new List<TextHighlighter>
        {
          highlighterFactory(length) with { TextAnimation = null }
        };
        
        textContentSegment.Text.ReplaceHighlighters(newHighlighters);
      }
    }

    return true;
  }

  private void FillTicketsIfPresentTo([NotNull] XmlElement parent, [NotNull] EntityWithContentSegments entity)
  {
    var ticketSection = parent.ChildElements().FirstOrDefault(child => child.Name == DocCommentsBuilderUtil.TicketsSectionTagName);
    var tickets = ticketSection switch
    {
      { } => TryProcessTicketsContentSection(ticketSection),
      _ => EmptyList<ITicketContentSegment>.Enumerable
    };

    entity.ContentSegments.Segments.AddRange(tickets);
  }

  [NotNull]
  private IEnumerable<ITicketContentSegment> TryProcessTicketsContentSection([NotNull] XmlElement ticketsTag)
  {
    if (ticketsTag.Name != DocCommentsBuilderUtil.TicketsSectionTagName)
    {
      return EmptyList<ITicketContentSegment>.Enumerable;
    }

    var tickets = new LocalList<ITicketContentSegment>();
    foreach (var child in ticketsTag.ChildElements())
    {
      if (TryCreateTicketContentSegment(child) is { } ticket)
      {
        tickets.Add(ticket);
      }
    }

    return tickets.ResultingList();
  }

  [CanBeNull]
  private ITicketContentSegment TryCreateTicketContentSegment([NotNull] XmlElement element)
  {
    if (element.Name != DocCommentsBuilderUtil.TicketTagName) return null;
    if (element.GetAttributeNode(DocCommentsBuilderUtil.TicketSourceAttrName) is not { } attribute) return null;

    var attributeValue = attribute.Value;
    var reference = TicketSourceParserUtil.TryParse(mySolution, attributeValue) ??
                    new HttpDomainReference(attributeValue, attributeValue);
    
    var description = new EntityWithContentSegments(ContentSegments.CreateEmpty());
    ProcessEntityWithContentSegments(description, element, false);
    
    return new TicketContentSegment(description, reference);
  }

  private record CodeFragment([NotNull] IHighlightedText PreliminaryText, int HighlightingRequestId);
  
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
      var id = myCodeFragmentHighlightingManager?.AddRequestForHighlighting(request) ?? 0;
      
      var preliminaryHighlighter = myLanguageManager.GetService<IPreliminaryCodeHighlighter>(file.Language);
      var highlightedText = HighlightedText.CreateEmptyText();
      var context = new CodeHighlightingContext(highlightedText, new UserDataHolder());
      
      node.ProcessThisAndDescendants(preliminaryHighlighter, context);

      return new CodeFragment(highlightedText, id); 
    }

    return CreateDefaultFragment();
  }
}