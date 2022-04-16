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
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;
using ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation;

public record struct NamedEntityExtraction(DocumentRange DocumentRange, NameWithKind NameWithKind);

internal static class NavigationUtil
{
  [CanBeNull]
  public static NamedEntityExtraction? TryExtractNameFromReference([NotNull] IDataContext dataContext)
  {
    var token = TryFindTokenUnderCaret(dataContext, out var caretDocumentOffset);
    if (token is null || !caretDocumentOffset.HasValue) return null;

    var caretOffset = caretDocumentOffset.Value;
    return TryExtractNameFromReferenceAttributeValue(token, caretOffset) ??
           TryExtractNameFromInlinedReference(token, caretOffset);
  }

  [CanBeNull]
  private static NamedEntityExtraction? TryExtractNameFromReferenceAttributeValue(
    [NotNull] ITreeNode tokenUnderCaret,
    DocumentOffset caretDocumentOffset)
  {
    return TryExtractNameFromAttribute(
      tokenUnderCaret, caretDocumentOffset, DocCommentsBuilderUtil.TryExtractNameFromPossibleReferenceSourceAttribute);
  }

  private static NamedEntityExtraction? TryExtractNameFromAttribute(
    [NotNull] ITreeNode tokenUnderCaret,
    DocumentOffset caretDocumentOffset,
    Func<IXmlAttribute, NameWithKind?> extractor)
  {
    if (tokenUnderCaret?.TryFindDocCommentBlock() is not { } docCommentBlock)
      return null;

    if (docCommentBlock.TryGetXmlToken(caretDocumentOffset) is not IXmlAttributeValue { Parent: { } parent })
      return null;

    if (parent is not IXmlAttribute attribute || extractor(attribute) is not { } extraction)
    {
      return null;
    }

    return new NamedEntityExtraction(attribute.Value.GetDocumentRange(), extraction);
  }

  [CanBeNull]
  private static NamedEntityExtraction? TryExtractNameFromTag(
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

  public static NamedEntityExtraction? TryExtractNameFromNamedEntity(IDataContext dataContext)
  {
    var token = TryFindTokenUnderCaret(dataContext, out var caretDocumentOffset);
    if (token is null || !caretDocumentOffset.HasValue) return null;

    return TryExtractNameFromTag(token, caretDocumentOffset.Value) ??
           TryExtractNameFromInlineComment(token);
  }

  private static NamedEntityExtraction? TryExtractNameFromInlineComment([NotNull] ITreeNode token)
  {
    if (NamesResolveUtil.TryFindNearestCommentNode(token) is not { } commentNode) return null;
    if (NamesResolveUtil.TryFindOneNameDeclarationIn(commentNode) is not { } nameWithKind) return null;

    return new NamedEntityExtraction(commentNode.GetDocumentRange(), nameWithKind);
  }

  [CanBeNull]
  private static NamedEntityExtraction? TryExtractNameFromInlinedReference(
    [NotNull] ITreeNode token,
    DocumentOffset caretOffset)
  {
    if (NamesResolveUtil.TryFindAnyCommentNode(token) is not { } commentNode) return null;
    if (LanguageManager.Instance.TryGetService<InlineReferenceCommentCreator>(token.Language) is not { } creator)
      return null;

    if (creator.TryExtractInlineReferenceInfo(commentNode) is not { } info) return null;
    if (info.NameRange.Contains(caretOffset))
    {
      return new NamedEntityExtraction(info.NameRange, info.NameWithKind);
    }

    return null;
  }

  public static void FindReferencesForNamedEntity(
    [NotNull] IDataContext dataContext, [CanBeNull] INavigationExecutionHost host = null)
  {
    if (TryExtractNameFromNamedEntity(dataContext) is not { } extraction) return;
    if (dataContext.GetData(ProjectModelDataConstants.SOLUTION) is not { } solution) return;

    var occurrences = NamesResolveUtil.FindAllReferencesForNamedEntity(extraction.NameWithKind, solution)
      .Select(dto => new NamedEntityOccurence(dto.SourceFile, dto.Range.StartOffset))
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
    var resolveResult = NamesResolveUtil.ResolveName(extraction.NameWithKind, resolveContext);
    if (resolveResult is not NamedEntityDomainResolveResult invariantResolveResult)
    {
      host ??= solution.GetComponent<INavigationExecutionHost>();
      host.ShowToolip(context, "Failed to resolve name for this reference");
      return;
    }

    var sourceFile = invariantResolveResult.ParentCommentBlock.GetSourceFile();
    var offset = invariantResolveResult.NameDeclarationDocumentOffset;

    sourceFile.Navigate(new TextRange(offset.Offset), true);
  }
}