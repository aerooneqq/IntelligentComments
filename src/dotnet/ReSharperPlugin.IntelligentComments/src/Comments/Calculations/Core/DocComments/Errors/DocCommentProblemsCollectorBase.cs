using System;
using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.Util;
using JetBrains.Util.Logging;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Errors;

public record DocCommentErrorAnalyzerContext([NotNull] IDocCommentBlock AdjustedComment, [NotNull] List<HighlightingInfo> Highlightings);

public interface IDocCommentProblemsCollector : IRecursiveElementProcessor<DocCommentErrorAnalyzerContext>
{
  [NotNull] [ItemNotNull] ICollection<HighlightingInfo> Run([NotNull] IDocCommentBlock comment);
}
 
public abstract class DocCommentProblemsCollectorBase : IDocCommentProblemsCollector
{
  [NotNull] private static readonly ILogger ourLogger = Logger.GetLogger<DocCommentProblemsCollectorBase>();
  [NotNull] private readonly IDictionary<string, Action<IXmlTag, DocCommentErrorAnalyzerContext>> myTagsProcessors;
  
  
  protected DocCommentProblemsCollectorBase()
  {
    myTagsProcessors = new Dictionary<string, Action<IXmlTag, DocCommentErrorAnalyzerContext>>
    {
      [DocCommentsBuilderUtil.ImageTagName] = ProcessImage,
      [DocCommentsBuilderUtil.InvariantTagName] = ProcessInvariant,
      [DocCommentsBuilderUtil.ReferenceTagName] = ProcessReference,
      [DocCommentsBuilderUtil.TodoTagName] = ProcessToDo,
      [DocCommentsBuilderUtil.HackTagName] = ProcessHack
    };
  }
  
  
  public ICollection<HighlightingInfo> Run(IDocCommentBlock comment)
  {
    if (DocCommentsBuilderUtil.TryGetAdjustedComment(comment) is not { } adjustedComment)
    {
      return EmptyList<HighlightingInfo>.Instance;
    }
    
    var psiHelper = LanguageManager.Instance.TryGetCachedService<IPsiHelper>(comment.Language);
    if (psiHelper is null) return EmptyList<HighlightingInfo>.Instance;

    var xmlDocPsi = psiHelper.GetXmlDocPsi(adjustedComment);
    var highlightings = new List<HighlightingInfo>();
    var context = new DocCommentErrorAnalyzerContext(adjustedComment, highlightings);
    
    xmlDocPsi?.XmlFile.ProcessThisAndDescendants(this, context);

    var isInheritDoc = DocCommentsBuilderUtil.IsInheritDocComment(comment);
    if (isInheritDoc && highlightings.Count > 0)
    {
      var firstErrorMessage = highlightings.First().Highlighting.ToolTip;
      highlightings.Clear();
      var message = $"Parent comment contains errors, the first one: \"{firstErrorMessage}\"";
      AddError(comment.GetDocumentRange(), message, context);
    }

    return highlightings;
  }

  public bool IsProcessingFinished(DocCommentErrorAnalyzerContext context) => false;

  public void ProcessBeforeInterior(ITreeNode element, DocCommentErrorAnalyzerContext context)
  {
    if (element is not IXmlTag xmlTag) return;
    if (myTagsProcessors.TryGetValue(xmlTag.GetTagName(), out var processor))
    {
      processor.Invoke(xmlTag, context);
    }
  }

  private void ProcessHack(IXmlTag hackTag, DocCommentErrorAnalyzerContext context)
  {
    if (!CheckIfTagInTopmostContext(hackTag, context)) return;
    if (!CheckThatNameOccursNotMoreThanOnce(hackTag, context)) return;
    if (!CheckThatTagHaveOnlyAttributesFromPredefinedSet(hackTag, DocCommentsBuilderUtil.PossibleNamedEntityTagAttributes, context)) return;
    if (!CheckThatAllChildrenAreTags(hackTag, context, DocCommentsBuilderUtil.PossibleInnerFirstLevelTagsOfHack))
      return;

    CheckTicketsSectionsIfPresent(hackTag, context);
  }

  private bool CheckTicketsSectionsIfPresent([NotNull] IXmlTag parentTag, [NotNull] DocCommentErrorAnalyzerContext context)
  {
    var ticketsTag = parentTag.InnerTags.FirstOrDefault(
      child => child.Header.Name.XmlName == DocCommentsBuilderUtil.TicketsSectionTagName);
    
    return ticketsTag is null || CheckTicketsTag(ticketsTag, context);
  }

  private bool CheckIfTagInTopmostContext([NotNull] IXmlTag tag, DocCommentErrorAnalyzerContext context)
  {
    if (IsTopmostContext(tag)) return true;
    
    var tagName = tag.Header.Name.XmlName;
    AddError(tag.GetDocumentRange(), $"Tag {tagName} must be used only in topmost context", context);
    return false;
  }
  
  private static bool IsTopmostContext([NotNull] IXmlTag xmlTag)
  {
    return xmlTag.Parent is IXmlFile;
  }
  
  private void ProcessToDo([NotNull] IXmlTag todoTag, [NotNull] DocCommentErrorAnalyzerContext context)
  {
    if (!CheckIfTagInTopmostContext(todoTag, context)) return;
    if (!CheckThatTagHaveOnlyAttributesFromPredefinedSet(todoTag, DocCommentsBuilderUtil.PossibleNamedEntityTagAttributes, context)) return;
    if (!CheckThatAllChildrenAreTags(todoTag, context, DocCommentsBuilderUtil.PossibleInnerFirstLevelTagsOfTodo)) return;
    if (!CheckThatNameOccursNotMoreThanOnce(todoTag, context)) return;
    CheckTicketsSectionsIfPresent(todoTag, context);
  }

  private bool CheckTicketsTag([NotNull] IXmlTag xmlTag, [NotNull] DocCommentErrorAnalyzerContext context)
  {
    return CheckThatAllChildrenAreTags(xmlTag, context, DocCommentsBuilderUtil.PossibleInnerFirstLevelTagsOfTicketsSection);
  }

  private bool CheckThatAllChildrenAreTags(
    [NotNull] IXmlTag parent, 
    [NotNull] DocCommentErrorAnalyzerContext context,
    [CanBeNull] IReadOnlySet<string> possibleTagsNames = null)
  {
    if (parent.IsEmptyTag) return true;

    var anyError = false;
    foreach (var textToken in parent.InnerTextTokens)
    {
      if (!textToken.GetText().IsNullOrWhitespace())
      {
        anyError = true;
        AddError(textToken.GetDocumentRange(), "The text is not allowed here", context);
      }
    }
    
    if (possibleTagsNames is { })
    {
      foreach (var tag in parent.InnerTags)
      {
        if (!possibleTagsNames.Contains(tag.Header.Name.XmlName))
        {
          anyError = true;
          AddError(tag.Header.GetDocumentRange(), $"Invalid tag", context);
        }
      }
    }

    return !anyError;
  }

  private void ProcessImage([NotNull] IXmlTag imageTag, [NotNull] DocCommentErrorAnalyzerContext context)
  {
    CheckAttributePresenceAndNonEmptyValue(imageTag, DocCommentsBuilderUtil.ImageSourceAttrName, context);
  }

  private static void AddError(DocumentRange range, [NotNull] string message, [NotNull] DocCommentErrorAnalyzerContext context)
  {
    if (!range.IsValid()) return;
    context.Highlightings.Add(CommentErrorHighlighting.CreateInfo(message, range));
  }

  private bool CheckAttributePresenceAndNonEmptyValue(
    [NotNull] IXmlTag tag, [NotNull] string attributeName, [NotNull] DocCommentErrorAnalyzerContext context)
  {
    var haveSourceTag = CheckAttributePresence(tag, attributeName, context);
    return haveSourceTag && CheckAttributeValueIsNotEmpty(tag, attributeName, context);
  }

  private static bool CheckAttributePresence(
    [NotNull] IXmlTag tag, [NotNull] string attributeName, [NotNull] DocCommentErrorAnalyzerContext context)
  {
    if (tag.GetAttribute(attributeName) is { }) return true;

    var message = $"Tag \"{tag.GetTagName()}\" must have attribute \"{attributeName}\" to be set";
    AddError(tag.GetDocumentRange(), message, context);
    return false;
  }

  private bool CheckAttributeValueIsNotEmpty(
    [NotNull] IXmlTag tag, [NotNull] string attributeName, [NotNull] DocCommentErrorAnalyzerContext context)
  {
    var attribute = tag.GetAttribute(attributeName);
    var attributeValue = attribute?.UnquotedValue;
    Assertion.Assert(attributeValue is { }, "source is { }");

    if (attributeValue.Length != 0) return true;

    var message = $"Tag \"{tag.GetTagName()}\" must have attribute \"{attributeName}\" to have non-empty value";
    AddError(attribute.GetDocumentRange(), message, context);
    return false;
  }

  private void ProcessReference([NotNull] IXmlTag referenceTag, [NotNull] DocCommentErrorAnalyzerContext context)
  {
    if (!CheckThatReferenceHasExactlyOneOfNeededTags(referenceTag, context)) return;
    if (DocCommentsBuilderUtil.TryExtractOneReferenceNameKindFromReferenceTag(referenceTag) is not { } extraction) return;
    if (DocCommentsBuilderUtil.TryGetOneReferenceSourceAttribute(referenceTag) is not { } attribute) return;
    if (!CheckIfTagInTopmostContext(referenceTag, context)) return;
    
    CheckIfNameReferenceSourceIsResolved(extraction, attribute, context);
  }

  private void ProcessInvariant([NotNull] IXmlTag invariantTag, [NotNull] DocCommentErrorAnalyzerContext context)
  {
    if (!CheckIfTagInTopmostContext(invariantTag, context)) return;
    if (!CheckThatTagHaveOnlyAttributesFromPredefinedSet(invariantTag, DocCommentsBuilderUtil.PossibleNamedEntityTagAttributes, context)) return;
    
    CheckThatNameOccursNotMoreThanOnce(invariantTag, context);
  }

  private bool CheckThatReferenceHasExactlyOneOfNeededTags([NotNull] IXmlTag referenceTag, [NotNull] DocCommentErrorAnalyzerContext context)
  {
    var needAttrsCount = 0;
    var tagRange = referenceTag.GetDocumentRange();
    
    foreach (var attribute in referenceTag.GetAttributes())
    {
      if (DocCommentsBuilderUtil.PossibleReferenceTagSourceAttributes.Contains(attribute.AttributeName))
      {
        if (needAttrsCount > 0)
        {
          AddError(tagRange, "Reference tag can not reference more than one entity", context);
          return false;
        }

        ++needAttrsCount;
        continue;
      }
      
      AddError(tagRange, $"Reference tag can not contain attribute {attribute.AttributeName}", context);
      return false;
    }

    if (needAttrsCount != 1)
    {
      var possibleAttrs = DocCommentsBuilderUtil.PossibleReferenceTagAttributesPresentation;
      AddError(tagRange, $"Reference tag must contain exactly one reference-source attribute ({possibleAttrs})", context);
      return false;
    }
    
    return true;
  }

  private bool CheckThatTagHaveOnlyAttributesFromPredefinedSet(
    [NotNull] IXmlTag tag, 
    [NotNull] IReadOnlySet<string> possibleAttributes, 
    [NotNull] DocCommentErrorAnalyzerContext context)
  {
    var visitedAttributes = new HashSet<string>();
    var tagName = tag.Header.Name.XmlName;
    
    foreach (var attribute in tag.GetAttributes())
    {
      var attrName = attribute.AttributeName;
      if (possibleAttributes.Contains(attrName))
      {
        if (visitedAttributes.Contains(attrName))
        {
          AddError(attribute.GetDocumentRange(), $"{tagName} already defined attribute {attrName}", context);
          return false;
        }

        visitedAttributes.Add(attrName);
        continue;
      }
      
      AddError(attribute.GetDocumentRange(), $"{tagName} can not contain attribute {attrName}", context);
      return false;
    }

    return true;
  }

  private bool CheckThatNameOccursNotMoreThanOnce([NotNull] IXmlTag namedEntityTag, [NotNull] DocCommentErrorAnalyzerContext context)
  {
    if (DocCommentsBuilderUtil.TryGetCommonNameAttribute(namedEntityTag) is not { } nameAttribute) return true;
    if (DocCommentsBuilderUtil.TryExtractNameFrom(namedEntityTag) is not var (name, nameKind)) return true;

    var cache = NamesCacheUtil.GetCacheFor(context.AdjustedComment.GetSolution(), nameKind);
    var invariantNameCount = cache.GetNameCount(name);

    if (invariantNameCount == 1) return true;

    var range = nameAttribute.Value.GetDocumentRange();
    AddError(range, $"The {nameKind} name \"{name}\" must occur only once in solution", context);
    return false;
  }

  private bool CheckIfNameReferenceSourceIsResolved(
    NameWithKind extraction,
    [NotNull] IXmlAttribute attribute,
    [NotNull] DocCommentErrorAnalyzerContext context)
  {
    var (name, nameKind) = extraction;
    var reference = new NamedEntityDomainReference(name, nameKind);
    var adjustedComment = context.AdjustedComment;
    var solution = adjustedComment.GetSolution();
    var document = adjustedComment.GetSourceFile()?.Document;
    
    if (document is null)
    {
      ourLogger.Error($"Failed to get document for \"{context.AdjustedComment.GetContainingFile()}\"");
      return false;
    }

    if (reference.Resolve(new DomainResolveContextImpl(solution, document)) is InvalidDomainResolveResult)
    {
      var text = $"Failed to resolve {nameKind} \"{name}\"";
      AddError(attribute.Value.GetDocumentRange(), text, context);
      return false;
    }

    return true;
  }
  
  public bool InteriorShouldBeProcessed(ITreeNode element, DocCommentErrorAnalyzerContext context) => true;

  public void ProcessAfterInterior(ITreeNode element, DocCommentErrorAnalyzerContext context)
  {
  }
}