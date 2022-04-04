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
using ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

[StaticSeverityHighlighting(Severity.ERROR, typeof(CommentError), OverlapResolve = OverlapResolveKind.ERROR)]
public class CommentError : IHighlighting
{
  private readonly DocumentRange myRange;
  
  
  public string ToolTip { get; }
  public string ErrorStripeToolTip { get; }


  public CommentError(DocumentRange range, string errorMessage)
  {
    myRange = range;
    ToolTip = errorMessage;
    ErrorStripeToolTip = errorMessage;
  }

  
  public bool IsValid() => myRange.IsValid();
  public DocumentRange CalculateRange() => myRange;
}

public record Context([NotNull] IDocCommentBlock AdjustedComment, [NotNull] List<HighlightingInfo> Highlightings);

public interface ICommentProblemsCollector : IRecursiveElementProcessor<Context>
{
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
    };
  }

  
  private void ProcessToDo([NotNull] IXmlTag tag, [NotNull] Context context)
  {
    if (!CheckThatAllChildrenAreTags(tag, context, DocCommentsBuilderUtil.PossibleInnerFirstLevelTagsOfTodo)) return;

    var ticketsTag = tag.InnerTags.FirstOrDefault(child => child.Header.Name.XmlName == DocCommentsBuilderUtil.TicketsSectionTagName);
    if (ticketsTag is { } && !CheckTicketsTag(ticketsTag, context)) return;
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

  private void ProcessImage([NotNull] IXmlTag imageTag, [NotNull] Context context)
  {
    CheckAttributePresenceAndNonEmptyValue(imageTag, DocCommentsBuilderUtil.ImageSourceAttrName, context);
  }

  private void AddError(DocumentRange range, [NotNull] string message, [NotNull] Context context)
  {
    var adjustedMessage = $"[IC]: {message}";
    var error = new CommentError(range, adjustedMessage);
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

    var referenceSourceAttribute = referenceTag.GetAttributes()
      .FirstOrDefault(attr => DocCommentsBuilderUtil.PossibleReferenceTagAttributes.Contains(attr.AttributeName));
    
    Assertion.AssertNotNull(referenceSourceAttribute, "referenceSourceAttribute != null");

    if (referenceSourceAttribute.AttributeName == DocCommentsBuilderUtil.InvariantReferenceSourceAttrName)
    {
      CheckIfInvariantReferenceSourceIsResolved(referenceTag, context);
    }
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
      if (DocCommentsBuilderUtil.PossibleReferenceTagAttributes.Contains(attribute.AttributeName))
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
    var cache = invariantTag.GetSolution().GetComponent<InvariantsNamesCache>();
    var invariantNameCount = cache.GetInvariantNameCount(name);

    if (invariantNameCount == 1) return true;

    var range = invariantNameAttribute.Value.GetDocumentRange();
    AddError(range, $"The invariant name \"{name}\" must occur only once in solution", context);
    return false;
  }

  private bool CheckIfInvariantReferenceSourceIsResolved([NotNull] IXmlTag referenceTag, [NotNull] Context context)
  {
    var referenceSourceAttr = referenceTag.GetAttribute(DocCommentsBuilderUtil.InvariantReferenceSourceAttrName);
    Assertion.AssertNotNull(referenceSourceAttr, "referenceSourceAttr is { }");

    var referenceSourceText = referenceSourceAttr.UnquotedValue;
    var reference = new InvariantDomainReference(referenceSourceText);
    var solution = context.AdjustedComment.GetSolution();
    var document = context.AdjustedComment.GetSourceFile()?.Document;
    
    if (document is null)
    {
      ourLogger.Error($"Failed to get document for \"{context.AdjustedComment.GetContainingFile()}\"");
      return false;
    }

    if (reference.Resolve(new DomainResolveContextImpl(solution, document)) is InvalidDomainResolveResult)
    {
      var text = $"Failed to resolve {DocCommentsBuilderUtil.InvariantReferenceSourceAttrName} \"{referenceSourceText}\"";
      AddError(referenceSourceAttr.Value.GetDocumentRange(), text, context);
      return false;
    }

    return true;
  }
  
  public bool InteriorShouldBeProcessed(ITreeNode element, Context context) => true;

  public void ProcessAfterInterior(ITreeNode element, Context context)
  {
  }
}