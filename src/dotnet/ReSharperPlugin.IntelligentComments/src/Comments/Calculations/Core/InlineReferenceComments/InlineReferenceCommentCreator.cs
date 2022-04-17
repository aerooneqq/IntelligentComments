using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;

public interface IInlineReferenceCommentCreator : ICommentFromNodeCreator
{
}

public abstract class InlineReferenceCommentCreator : IInlineReferenceCommentCreator, INamedEntitiesCommonFinder
{
  public int Priority => CommentFromNodeCreatorsPriorities.Default;

  
  [CanBeNull]
  public virtual CommentCreationResult? TryCreate([NotNull] ITreeNode node)
  {
    if (TryExtractInlineReferenceInfo(node) is not var ((name, nameKind), descriptionText, _)) return null;
    
    var description = HighlightedText.CreateEmptyText();
    var provider = LanguageManager.Instance.GetService<IHighlightersProvider>(node.Language);

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

  public abstract InlineReferenceCommentInfo? TryExtractInlineReferenceInfo([NotNull] ITreeNode node);
  public abstract InlineReferenceCommentInfo? TryExtractCompletionInlineReferenceInfo(
    [NotNull] ITreeNode node, DocumentOffset contextCaretDocumentOffset);

  public IEnumerable<CommonNamedEntityDescriptor> FindReferences(ITreeNode node, NameWithKind nameWithKind)
  {
    return FindReferencesOrAll(node, nameWithKind);
  }

  public IEnumerable<CommonNamedEntityDescriptor> FindAllReferences(ITreeNode node)
  {
    return FindReferencesOrAll(node, null);
  }

  public IEnumerable<CommonNamedEntityDescriptor> FindNames(ITreeNode node)
  {
    return EmptyList<CommonNamedEntityDescriptor>.Enumerable;
  }

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