using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.DocumentManagers;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Errors;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;

public abstract class InlineReferenceCommentOperations : ISpecialGroupOfLinesCommentsOperations, INamedEntitiesCommonFinder
{
  public int Priority => CommentFromNodeOperationsPriorities.Default;

  
  [CanBeNull]
  public virtual CommentCreationResult? TryCreate([NotNull] ITreeNode node)
  {
    if (TryExtractInlineReferenceInfo(node) is not var ((name, nameKind), descriptionText, _)) return null;
    
    var description = HighlightedText.CreateEmptyText();
    var provider = LanguageManager.Instance.GetService<IHighlightersProvider>(node.Language);

    [CanBeNull]
    TextHighlighter TryGetDocCommentHighlighter(int length) => provider.TryGetDocCommentHighlighter(length);
    
    if (descriptionText is { } && !descriptionText.IsNullOrWhitespace())
    {
      descriptionText = $", {descriptionText}";
      description.Add(new HighlightedText(descriptionText, TryGetDocCommentHighlighter(descriptionText.Length)));
    }

    var nameHighlighter = provider.TryGetDocCommentHighlighter(name.Length);
    if (nameHighlighter is { })
    {
      var domainReference = new NamedEntityDomainReference(name, nameKind);
      nameHighlighter = nameHighlighter with
      {
        References = new[] { domainReference },
        TextAnimation = UnderlineTextAnimation.Instance
      };
    }

    if (DocCommentsBuilderUtil.TryGetReferenceAttributeNameFrom(nameKind) is not { } attributeName) return null;
    
    var referenceToInvariantText = $"Reference to {attributeName}: ";
    var nameText = new HighlightedText(referenceToInvariantText, TryGetDocCommentHighlighter(referenceToInvariantText.Length));
    nameText.Add(new HighlightedText(name, nameHighlighter));

    var referenceContentSegment = new InlineReferenceContentSegment(nameText, description);
    var comment = new InlineReferenceComment(referenceContentSegment, node.GetDocumentRange());

    return new CommentCreationResult(comment, new[] { node });
  }

  public IEnumerable<CommentErrorHighlighting> FindErrors(ITreeNode node)
  {
    if (TryExtractInlineReferenceInfo(node) is not var (nameWithKind, _, nameRange)) 
      return EmptyList<CommentErrorHighlighting>.Enumerable;

    var document = nameRange.Document;
    if (document.TryGetSolution() is not { } solution) return EmptyList<CommentErrorHighlighting>.Enumerable;

    var domainResolveContext = new DomainResolveContextImpl(solution, document);
    if (NamesResolveUtil.ResolveName(nameWithKind, domainResolveContext) is NamedEntityDomainResolveResult)
      return EmptyList<CommentErrorHighlighting>.Enumerable;
    
    var message = $"Failed to resolve inline reference with name {nameWithKind.Name} with kind {nameWithKind.NameKind}";
    return new[] { CommentErrorHighlighting.Create(message, nameRange) };
  }

  public abstract bool CanBeStartOfSpecialGroupOfLineComments(ITreeNode node);

  public abstract InlineReferenceCommentInfo? TryExtractInlineReferenceInfo([NotNull] ITreeNode node);
  public abstract InlineReferenceCommentInfo? TryExtractCompletionInlineReferenceInfo(
    [NotNull] ITreeNode node, DocumentOffset contextCaretDocumentOffset);

  public IEnumerable<CommonNamedEntityDescriptor> FindReferences(ITreeNode node, NameWithKind nameWithKind) => FindReferencesOrAll(node, nameWithKind);
  public IEnumerable<CommonNamedEntityDescriptor> FindAllReferences(ITreeNode node) => FindReferencesOrAll(node, null);
  public IEnumerable<CommonNamedEntityDescriptor> FindNames(ITreeNode node) => EmptyList<CommonNamedEntityDescriptor>.Enumerable;

  private IEnumerable<CommonNamedEntityDescriptor> FindReferencesOrAll([NotNull] ITreeNode node, NameWithKind? nameWithKind)
  {
    if (TryExtractInlineReferenceInfo(node) is not { } info ||
        node.GetSourceFile() is not { } sourceFile ||
        (nameWithKind.HasValue && info.NameWithKind != nameWithKind.Value))
    {
      return EmptyList<CommonNamedEntityDescriptor>.Enumerable;
    }

    return new[] { new CommonNamedEntityDescriptor(sourceFile, info.NameRange, info.NameWithKind) };
  }
}

public record struct InlineReferenceCommentInfo(
  NameWithKind NameWithKind,
  [CanBeNull] string Description,
  DocumentRange NameRange
);