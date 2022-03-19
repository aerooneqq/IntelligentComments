using System;
using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.Util;
using System.Linq;
using JetBrains.ProjectModel;
using JetBrains.Util.Logging;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;

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

public record Context(IDocCommentBlock AdjustedComment);

public interface ICommentProblemsCollector : IRecursiveElementProcessor<Context>
{
}

public abstract class CommentProblemsCollectorBase : ICommentProblemsCollector
{
  [NotNull] private static readonly ILogger ourLogger = Logger.GetLogger<CommentProblemsCollectorBase>();
  
  [NotNull] private readonly IDocCommentBlock myInitialComment;
  [NotNull] private readonly List<HighlightingInfo> myHighlightings;
  [NotNull] private readonly IDictionary<string, Action<IXmlTag, Context>> myTagsProcessors;
  
  
  protected CommentProblemsCollectorBase([NotNull] IDocCommentBlock comment)
  {
    myInitialComment = comment;
    myHighlightings = new List<HighlightingInfo>();
    myTagsProcessors = new Dictionary<string, Action<IXmlTag, Context>>
    {
      [CommentsBuilderUtil.ImageTagName] = ProcessImage,
      [CommentsBuilderUtil.InvariantTagName] = ProcessInvariant,
      [CommentsBuilderUtil.ReferenceTagName] = ProcessReference
    };
  }


  [NotNull]
  public ICollection<HighlightingInfo> Run()
  {
    if (CommentsBuilderUtil.TryGetAdjustedComment(myInitialComment) is not { } adjustedComment)
    {
      return EmptyList<HighlightingInfo>.Instance;
    }
    
    var psiHelper = LanguageManager.Instance.TryGetCachedService<IPsiHelper>(myInitialComment.Language);
    if (psiHelper is null) return EmptyList<HighlightingInfo>.Instance;

    var xmlDocPsi = psiHelper.GetXmlDocPsi(adjustedComment);
    xmlDocPsi?.XmlFile.ProcessThisAndDescendants(this, new Context(adjustedComment));

    var isInheritDoc = CommentsBuilderUtil.IsInheritDocComment(myInitialComment);
    if (isInheritDoc && myHighlightings.Count > 0)
    {
      var firstErrorMessage = myHighlightings.First().Highlighting.ToolTip;
      myHighlightings.Clear();
      AddError(myInitialComment.GetDocumentRange(), $"Parent comment contains errors, the first one: \"{firstErrorMessage}\"");
    }

    return myHighlightings;
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
    CheckAttributePresenceAndNonEmptyValue(imageTag, CommentsBuilderUtil.ImageSourceAttrName);
  }

  private void AddError(DocumentRange range, [NotNull] string message)
  {
    var error = new CommentError(range, message);
    var info = new HighlightingInfo(range, error);
    myHighlightings.Add(info);
  }

  private bool CheckAttributePresenceAndNonEmptyValue([NotNull] IXmlTag tag, [NotNull] string attributeName)
  {
    var haveSourceTag = CheckAttributePresence(tag, attributeName);
    if (!haveSourceTag) return false;

    return CheckAttributeValueIsNotEmpty(tag, attributeName);
  }

  private bool CheckAttributePresence([NotNull] IXmlTag tag, [NotNull] string attributeName)
  {
    if (tag.GetAttribute(attributeName) is { }) return true;

    AddError(tag.GetDocumentRange(), $"Tag \"{tag.GetTagName()}\" must have attribute \"{attributeName}\" to be set");
    return false;
  }

  private bool CheckAttributeValueIsNotEmpty([NotNull] IXmlTag tag, [NotNull] string attributeName)
  {
    var attribute = tag.GetAttribute(attributeName);
    var attributeValue = attribute?.UnquotedValue;
    Assertion.Assert(attributeValue is { }, "source is { }");

    if (attributeValue.Length != 0) return true;

    var message = $"Tag \"{tag.GetTagName()}\" must have attribute \"{attributeName}\" to have non-empty value";
    AddError(attribute.GetDocumentRange(), message);
    return false;
  }

  private void ProcessReference([NotNull] IXmlTag referenceTag, [NotNull] Context context)
  {
    if (!CheckAttributePresenceAndNonEmptyValue(referenceTag, CommentsBuilderUtil.ReferenceSourceAttrName)) return;
    CheckIfReferenceSourceIsResolved(referenceTag, context);
  }

  private void ProcessInvariant([NotNull] IXmlTag invariantTag, [NotNull] Context context)
  {
    if (!CheckAttributePresenceAndNonEmptyValue(invariantTag, CommentsBuilderUtil.InvariantNameAttrName)) return;
    CheckThatInvariantNameOccursOnce(invariantTag);
  }

  private bool CheckThatInvariantNameOccursOnce([NotNull] IXmlTag invariantTag)
  {
    var invariantNameAttribute = CommentsBuilderUtil.TryGetInvariantAttribute(invariantTag);
    Assertion.AssertNotNull(invariantNameAttribute, "attribute != null");

    var name = CommentsBuilderUtil.GetInvariantName(invariantNameAttribute);
    var cache = invariantTag.GetSolution().GetComponent<InvariantsNamesCache>();
    var invariantNameCount = cache.GetInvariantNameCount(name);

    if (invariantNameCount == 1) return true;

    var range = invariantNameAttribute.Value.GetDocumentRange();
    AddError(range, $"The invariant name \"{name}\" must occur only once in solution");
    return false;
  }

  private bool CheckIfReferenceSourceIsResolved([NotNull] IXmlTag referenceTag, [NotNull] Context context)
  {
    var referenceSourceAttr = referenceTag.GetAttribute(CommentsBuilderUtil.ReferenceSourceAttrName);
    Assertion.Assert(referenceSourceAttr is { }, "referenceSourceAttr is { }");

    var referenceSourceText = referenceSourceAttr.UnquotedValue;
    var reference = new InvariantReference(referenceSourceText);
    var solution = context.AdjustedComment.GetSolution();
    var document = context.AdjustedComment.GetSourceFile()?.Document;
    
    if (document is null)
    {
      ourLogger.Error($"Failed to get document for \"{context.AdjustedComment.GetContainingFile()}\"");
      return false;
    }

    if (reference.Resolve(new ResolveContextImpl(solution, document)) is InvalidResolveResult)
    {
      var text = $"Failed to resolve referenceSource \"{referenceSourceText}\"";
      AddError(referenceSourceAttr.Value.GetDocumentRange(), text);
      return false;
    }

    return true;
  }
  
  public bool InteriorShouldBeProcessed(ITreeNode element, Context context) => true;

  public void ProcessAfterInterior(ITreeNode element, Context context)
  {
  }
}