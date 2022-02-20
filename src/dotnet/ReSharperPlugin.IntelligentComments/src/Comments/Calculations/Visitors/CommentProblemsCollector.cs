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

public class CommentProblemsCollector : IRecursiveElementProcessor<CommentProblemsCollector.Context>
{
  public record Context(IDocCommentBlock AdjustedComment);

  [NotNull] private readonly IDocCommentBlock myInitialComment;
  [NotNull] private readonly List<HighlightingInfo> myHighlightings;
  [NotNull] private readonly IDictionary<string,Action<IXmlTag>> myTagsProcessors;
  
  
  // ReSharper disable once NotNullMemberIsNotInitialized
  public CommentProblemsCollector([NotNull] IDocCommentBlock comment)
  {
    myInitialComment = comment;
    myHighlightings = new List<HighlightingInfo>();
    myTagsProcessors = new Dictionary<string, Action<IXmlTag>>
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
      processor.Invoke(xmlTag);
    }
  }

  private void ProcessImage([NotNull] IXmlTag imageTag)
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

  private void ProcessReference([NotNull] IXmlTag referenceTag)
  {
    CheckAttributePresenceAndNonEmptyValue(referenceTag, CommentsBuilderUtil.ReferenceSourceAttrName);
  }

  private void ProcessInvariant([NotNull] IXmlTag invariantTag)
  {
    CheckAttributePresenceAndNonEmptyValue(invariantTag, CommentsBuilderUtil.InvariantNameAttrName);
  }
  
  public bool InteriorShouldBeProcessed(ITreeNode element, Context context) => true;

  public void ProcessAfterInterior(ITreeNode element, Context context)
  {
  }
}