using System;
using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.Util;
using JetBrains.Util.Logging;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names.Invariants;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Errors;

public record Context([NotNull] IDocCommentBlock AdjustedComment, [NotNull] List<HighlightingInfo> Highlightings);

public interface ICommentProblemsCollector : IRecursiveElementProcessor<Context>
{
  ICollection<HighlightingInfo> Run([NotNull] IDocCommentBlock comment);
}
 
public abstract class CommentProblemsCollectorBase : ICommentProblemsCollector
{
  [NotNull] private static readonly ILogger ourLogger = Logger.GetLogger<CommentProblemsCollectorBase>();
  [NotNull] private readonly IDictionary<string, Action<IXmlTag, Context>> myTagsProcessors;
  
  
  protected CommentProblemsCollectorBase()
  {
    myTagsProcessors = new Dictionary<string, Action<IXmlTag, Context>>
    {
      [DocCommentsBuilderUtil.ImageTagName] = ProcessImage,
      [DocCommentsBuilderUtil.InvariantTagName] = ProcessInvariant,
      [DocCommentsBuilderUtil.ReferenceTagName] = ProcessReference,
      [DocCommentsBuilderUtil.TodoTagName] = ProcessToDo,
      [DocCommentsBuilderUtil.HackTagName] = ProcessHack
    };
  }
  
  
  [NotNull]
  public ICollection<HighlightingInfo> Run([NotNull] IDocCommentBlock comment)
  {
    if (DocCommentsBuilderUtil.TryGetAdjustedComment(comment) is not { } adjustedComment)
    {
      return EmptyList<HighlightingInfo>.Instance;
    }
    
    var psiHelper = LanguageManager.Instance.TryGetCachedService<IPsiHelper>(comment.Language);
    if (psiHelper is null) return EmptyList<HighlightingInfo>.Instance;

    var xmlDocPsi = psiHelper.GetXmlDocPsi(adjustedComment);
    var highlightings = new List<HighlightingInfo>();
    var context = new Context(adjustedComment, highlightings);
    
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

  public bool IsProcessingFinished(Context context) => false;

  public void ProcessBeforeInterior(ITreeNode element, Context context)
  {
    if (element is not IXmlTag xmlTag) return;
    if (myTagsProcessors.TryGetValue(xmlTag.GetTagName(), out var processor))
    {
      processor.Invoke(xmlTag, context);
    }
  }

  private void ProcessHack(IXmlTag hackTag, Context context)
  {
    if (!CheckThatAllChildrenAreTags(hackTag, context, DocCommentsBuilderUtil.PossibleInnerFirstLevelTagsOfHack))
      return;

    CheckTicketsSectionsIfPresent(hackTag, context);
  }

  private bool CheckTicketsSectionsIfPresent([NotNull] IXmlTag parentTag, [NotNull] Context context)
  {
    var ticketsTag = parentTag.InnerTags.FirstOrDefault(
      child => child.Header.Name.XmlName == DocCommentsBuilderUtil.TicketsSectionTagName);
    
    return ticketsTag is null || CheckTicketsTag(ticketsTag, context);
  }
  
  private void ProcessToDo([NotNull] IXmlTag todoTag, [NotNull] Context context)
  {
    if (!CheckThatAllChildrenAreTags(todoTag, context, DocCommentsBuilderUtil.PossibleInnerFirstLevelTagsOfTodo)) return;
    CheckTicketsSectionsIfPresent(todoTag, context);
  }

  private bool CheckTicketsTag([NotNull] IXmlTag xmlTag, [NotNull] Context context)
  {
    return CheckThatAllChildrenAreTags(xmlTag, context, DocCommentsBuilderUtil.PossibleInnerFirstLevelTagsOfTicketsSection);
  }

  private bool CheckThatAllChildrenAreTags(
    [NotNull] IXmlTag parent, 
    [NotNull] Context context,
    [CanBeNull] ISet<string> possibleTagsNames = null)
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

  private void ProcessImage([NotNull] IXmlTag imageTag, [NotNull] Context context)
  {
    CheckAttributePresenceAndNonEmptyValue(imageTag, DocCommentsBuilderUtil.ImageSourceAttrName, context);
  }

  private void AddError(DocumentRange range, [NotNull] string message, [NotNull] Context context)
  {
    if (!range.IsValid()) return;
    
    var adjustedMessage = $"[IC]: {message}";
    var error = new CommentErrorHighlighting(range, adjustedMessage);
    var info = new HighlightingInfo(range, error);
    context.Highlightings.Add(info);
  }

  private bool CheckAttributePresenceAndNonEmptyValue(
    [NotNull] IXmlTag tag, [NotNull] string attributeName, [NotNull] Context context)
  {
    var haveSourceTag = CheckAttributePresence(tag, attributeName, context);
    if (!haveSourceTag) return false;

    return CheckAttributeValueIsNotEmpty(tag, attributeName, context);
  }

  private bool CheckAttributePresence([NotNull] IXmlTag tag, [NotNull] string attributeName, [NotNull] Context context)
  {
    if (tag.GetAttribute(attributeName) is { }) return true;

    var message = $"Tag \"{tag.GetTagName()}\" must have attribute \"{attributeName}\" to be set";
    AddError(tag.GetDocumentRange(), message, context);
    return false;
  }

  private bool CheckAttributeValueIsNotEmpty(
    [NotNull] IXmlTag tag, [NotNull] string attributeName, [NotNull] Context context)
  {
    var attribute = tag.GetAttribute(attributeName);
    var attributeValue = attribute?.UnquotedValue;
    Assertion.Assert(attributeValue is { }, "source is { }");

    if (attributeValue.Length != 0) return true;

    var message = $"Tag \"{tag.GetTagName()}\" must have attribute \"{attributeName}\" to have non-empty value";
    AddError(attribute.GetDocumentRange(), message, context);
    return false;
  }

  private void ProcessReference([NotNull] IXmlTag referenceTag, [NotNull] Context context)
  {
    if (!CheckThatReferenceHasExactlyOneOfNeededTags(referenceTag, context)) return;
    if (DocCommentsBuilderUtil.TryExtractOneReferenceNameKindFromReferenceTag(referenceTag) is not { } extraction) return;
    if (DocCommentsBuilderUtil.TryGetOneReferenceSourceAttribute(referenceTag) is not { } attribute) return;

    CheckIfNameReferenceSourceIsResolved(extraction, attribute, context);
  }

  private void ProcessInvariant([NotNull] IXmlTag invariantTag, [NotNull] Context context)
  {
    if (!CheckAttributePresenceAndNonEmptyValue(invariantTag, DocCommentsBuilderUtil.InvariantNameAttrName, context)) 
      return;
    
    CheckThatInvariantNameOccursOnce(invariantTag, context);
  }

  private bool CheckThatReferenceHasExactlyOneOfNeededTags([NotNull] IXmlTag referenceTag, [NotNull] Context context)
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

  private bool CheckThatInvariantNameOccursOnce([NotNull] IXmlTag invariantTag, [NotNull] Context context)
  {
    var invariantNameAttribute = DocCommentsBuilderUtil.TryGetInvariantAttribute(invariantTag);
    Assertion.AssertNotNull(invariantNameAttribute, "attribute != null");

    var name = DocCommentsBuilderUtil.GetInvariantName(invariantNameAttribute);
    var cache = invariantTag.GetSolution().GetComponent<InvariantsNamesNamesCache>();
    var invariantNameCount = cache.GetNameCount(name);

    if (invariantNameCount == 1) return true;

    var range = invariantNameAttribute.Value.GetDocumentRange();
    AddError(range, $"The invariant name \"{name}\" must occur only once in solution", context);
    return false;
  }

  private bool CheckIfNameReferenceSourceIsResolved(
    NameWithKind extraction,
    [NotNull] IXmlAttribute attribute,
    [NotNull] Context context)
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
  
  public bool InteriorShouldBeProcessed(ITreeNode element, Context context) => true;

  public void ProcessAfterInterior(ITreeNode element, Context context)
  {
  }
}