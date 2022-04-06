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
using System;
using System.Linq;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;
using ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation;

internal static class NavigationUtil
{
  [CanBeNull]
  public static string TryExtractInvariantNameFromReference([NotNull] IDataContext dataContext)
  {
    var token = TryFindTokenUnderCaret(dataContext, out var caretDocumentOffset);
    if (token is null || !caretDocumentOffset.HasValue) return null;

    var caretOffset = caretDocumentOffset.Value;
    return TryExtractAttributeValueFromTag(token, caretOffset, DocCommentsBuilderUtil.IsInvariantReferenceSourceAttribute) ??
           TryExtractInvariantNameFromInlinedReference(token, caretOffset);
  }

  [CanBeNull]
  private static string TryExtractAttributeValueFromTag(
    [NotNull] ITreeNode tokenUnderCaret,
    DocumentOffset caretDocumentOffset,
    [NotNull] Func<IXmlAttribute, bool> attributeValidityChecker)
  {
    if (tokenUnderCaret?.TryFindDocCommentBlock() is not { } docCommentBlock) 
      return null;
    
    if (docCommentBlock.TryGetXmlToken(caretDocumentOffset) is not IXmlAttributeValue { Parent: { } parent } value)
      return null;

    if (parent is not IXmlAttribute attribute || !attributeValidityChecker(attribute)) 
      return null;
    
    return value.UnquotedValue;
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
  
  public static string TryExtractInvariantNameFromInvariant(IDataContext dataContext)
  {
    var token = TryFindTokenUnderCaret(dataContext, out var caretDocumentOffset);
    if (token is null || !caretDocumentOffset.HasValue) return null;

    var caretOffset = caretDocumentOffset.Value;
    return TryExtractAttributeValueFromTag(token, caretOffset, DocCommentsBuilderUtil.IsInvariantNameAttribute);
  }

  [CanBeNull]
  private static string TryExtractInvariantNameFromInlinedReference(
    [NotNull] ITreeNode token, 
    DocumentOffset caretOffset)
  {
    if (InvariantResolveUtil.TryFindAnyCommentNode(token) is not { } commentNode) return null;
    if (LanguageManager.Instance.TryGetService<InlineReferenceCommentCreator>(token.Language) is not { } creator)
      return null;

    if (creator.TryExtractInlineReferenceInfo(commentNode) is not { } info) return null;
    if (caretOffset >= info.InvariantNameOffset &&
        caretOffset.Offset <= info.InvariantNameOffset.Offset + info.InvariantName.Length)
    {
      return info.InvariantName;
    }

    return null;
  }

  public static void FindReferencesToInvariant(
    [NotNull] IDataContext dataContext, [CanBeNull] INavigationExecutionHost host = null)
  {
    if (NavigationUtil.TryExtractInvariantNameFromInvariant(dataContext) is not { } invariantName) return;
    if (dataContext.GetData(ProjectModelDataConstants.SOLUTION) is not { } solution) return;
    
    var occurrences = InvariantResolveUtil.FindAllReferencesForInvariantName(invariantName, solution)
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
    if (TryExtractInvariantNameFromReference(context) is not { } name) return;
    if (context.GetData(ProjectModelDataConstants.SOLUTION) is not { } solution) return;
    if (context.GetData(DocumentModelDataConstants.DOCUMENT) is not { } document) return;

    var resolveContext = new DomainResolveContextImpl(solution, document);
    var resolveResult = InvariantResolveUtil.ResolveInvariantByName(name, resolveContext);
    if (resolveResult is not InvariantDomainResolveResult invariantResolveResult)
    {
      host ??= solution.GetComponent<INavigationExecutionHost>();
      host.ShowToolip(context, "Failed to resolve invariant for this reference");
      return;
    }

    var sourceFile = invariantResolveResult.ParentDocCommentBlock.GetSourceFile();
    var offset = invariantResolveResult.InvariantDocumentOffset;

    sourceFile.Navigate(new TextRange(offset.Offset), true);
  }
}