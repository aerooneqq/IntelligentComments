using System;
using JetBrains.Annotations;
using JetBrains.Application.DataContext;
using JetBrains.DocumentModel;
using JetBrains.DocumentModel.DataContext;
using JetBrains.ProjectModel;
using JetBrains.ProjectModel.DataContext;
using JetBrains.ReSharper.Feature.Services.Navigation.ContextNavigation;
using JetBrains.ReSharper.Feature.Services.Navigation.NavigationExtensions;
using JetBrains.ReSharper.Feature.Services.Occurrences;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.TextControl.DataContext;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;
using ReSharperPlugin.IntelligentComments.Comments.Navigation.FindReferences;
using System.Linq;
using ReSharperPlugin.IntelligentComments.Comments.Caches;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;
using ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation;

internal static class NavigationUtil
{
  [CanBeNull]
  public static NameExtraction? TryExtractNameFromReference([NotNull] IDataContext dataContext)
  {
    var token = TryFindTokenUnderCaret(dataContext, out var caretDocumentOffset);
    if (token is null || !caretDocumentOffset.HasValue) return null;

    var caretOffset = caretDocumentOffset.Value;
    return TryExtractNameFromReferenceAttributeValue(token, caretOffset) ??
           TryExtractInvariantNameFromInlinedReference(token, caretOffset);
  }

  [CanBeNull]
  private static NameExtraction? TryExtractNameFromReferenceAttributeValue(
    [NotNull] ITreeNode tokenUnderCaret,
    DocumentOffset caretDocumentOffset)
  {
    return TryExtractNameFromAttribute(
      tokenUnderCaret, caretDocumentOffset, DocCommentsBuilderUtil.TryExtractNameFromPossibleReferenceSourceAttribute);
  }

  private static NameExtraction? TryExtractNameFromAttribute(
    [NotNull] ITreeNode tokenUnderCaret,
    DocumentOffset caretDocumentOffset,
    Func<IXmlAttribute, NameExtraction?> extractor)
  {
    if (tokenUnderCaret?.TryFindDocCommentBlock() is not { } docCommentBlock)
      return null;

    if (docCommentBlock.TryGetXmlToken(caretDocumentOffset) is not IXmlAttributeValue { Parent: { } parent })
      return null;

    if (parent is not IXmlAttribute attribute || extractor(attribute) is not { } extraction)
    {
      return null;
    }

    return extraction;
  }

  [CanBeNull]
  private static NameExtraction? TryExtractNameFromTag(
    [NotNull] ITreeNode tokenUnderCaret,
    DocumentOffset caretDocumentOffset)
  {
    return TryExtractNameFromAttribute(tokenUnderCaret, caretDocumentOffset, attr =>
    {
      if (attr.Parent?.Parent is not IXmlTag xmlTag) return null;
      return DocCommentsBuilderUtil.TryExtractNameFrom(xmlTag);
    });
  }

  [CanBeNull]
  private static ITreeNode TryFindTokenUnderCaret(IDataContext dataContext, out DocumentOffset? caretDocumentOffset)
  {
    caretDocumentOffset = null;

    if (dataContext.GetData(TextControlDataConstants.TEXT_CONTROL) is not { } editor) return null;
    if (dataContext.GetData(ProjectModelDataConstants.SOLUTION) is not { } solution) return null;
    if (editor.Document.GetPsiSourceFile(solution)?.GetPrimaryPsiFile() is not ICSharpFile psiFile) return null;

    var docOffset = editor.Caret.Position.Value.ToDocOffset();
    caretDocumentOffset = new DocumentOffset(editor.Document, (int)docOffset);
    var range = psiFile.Translate(new DocumentRange(caretDocumentOffset.Value));
    return psiFile.FindTokenAt(range.StartOffset);
  }

  public static NameExtraction? TryExtractNameFromNamedEntity(IDataContext dataContext)
  {
    var token = TryFindTokenUnderCaret(dataContext, out var caretDocumentOffset);
    if (token is null || !caretDocumentOffset.HasValue) return null;

    return TryExtractNameFromTag(token, caretDocumentOffset.Value);
  }

  [CanBeNull]
  private static NameExtraction? TryExtractInvariantNameFromInlinedReference(
    [NotNull] ITreeNode token,
    DocumentOffset caretOffset)
  {
    if (NamesResolveUtil.TryFindAnyCommentNode(token) is not { } commentNode) return null;
    if (LanguageManager.Instance.TryGetService<InlineReferenceCommentCreator>(token.Language) is not { } creator)
      return null;

    if (creator.TryExtractInlineReferenceInfo(commentNode) is not { } info) return null;
    if (caretOffset >= info.InvariantNameOffset &&
        caretOffset.Offset <= info.InvariantNameOffset.Offset + info.Name.Length)
    {
      return new NameExtraction(info.Name, NameKind.Invariant);
    }

    return null;
  }

  public static void FindReferencesForNamedEntity(
    [NotNull] IDataContext dataContext, [CanBeNull] INavigationExecutionHost host = null)
  {
    if (NavigationUtil.TryExtractNameFromNamedEntity(dataContext) is not { } extraction) return;
    if (dataContext.GetData(ProjectModelDataConstants.SOLUTION) is not { } solution) return;

    var occurrences = NamesResolveUtil.FindAllReferencesForNamedEntity(extraction, solution)
      .Select(dto => new InvariantReferenceOccurence(dto.SourceFile, dto.Offset))
      .Select(o => (IOccurrence)o)
      .ToList();

    host ??= solution.GetComponent<INavigationExecutionHost>();
    host.ShowContextPopupMenu(
      dataContext,
      occurrences,
      () => new MySearchOccurrenceBrowserDescriptor(new ReferencesSearchRequest(occurrences, solution)),
      OccurrencePresentationOptions.DefaultOptions,
      false,
      "References which reference this invariant");
  }

  public static void NavigateToInvariantIfFound(
    [NotNull] IDataContext context, [CanBeNull] INavigationExecutionHost host = null)
  {
    if (TryExtractNameFromReference(context) is not { } extraction) return;
    if (context.GetData(ProjectModelDataConstants.SOLUTION) is not { } solution) return;
    if (context.GetData(DocumentModelDataConstants.DOCUMENT) is not { } document) return;

    var resolveContext = new DomainResolveContextImpl(solution, document);
    var resolveResult = NamesResolveUtil.ResolveName(extraction.Name, resolveContext, extraction.NameKind);
    if (resolveResult is not NamedEntityDomainResolveResult invariantResolveResult)
    {
      host ??= solution.GetComponent<INavigationExecutionHost>();
      host.ShowToolip(context, "Failed to resolve name for this reference");
      return;
    }

    var sourceFile = invariantResolveResult.ParentDocCommentBlock.GetSourceFile();
    var offset = invariantResolveResult.InvariantDocumentOffset;

    sourceFile.Navigate(new TextRange(offset.Offset), true);
  }
}