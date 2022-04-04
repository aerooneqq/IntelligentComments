using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;

public interface IInlineReferenceCommentCreator : ICommentFromNodeCreator
{
}

public abstract class InlineReferenceCommentCreator : IInlineReferenceCommentCreator, IReferenceInCommentFinder
{
  [CanBeNull]
  public virtual CommentCreationResult? TryCreate([NotNull] ITreeNode node)
  {
    if (TryExtractInlineReferenceInfo(node) is not var (referenceName, descriptionText, _)) return null;
    
    var description = HighlightedText.CreateEmptyText();
    var provider = LanguageManager.Instance.GetService<IHighlightersProvider>(node.Language);

    TextHighlighter TryGetDocCommentHighlighter(int length) => provider.TryGetDocCommentHighlighter(length);
    
    if (descriptionText is { } && !descriptionText.IsNullOrWhitespace())
    {
      descriptionText = $", {descriptionText}";
      description.Add(new HighlightedText(descriptionText, TryGetDocCommentHighlighter(descriptionText.Length)));
    }

    var nameHighlighter = provider.TryGetDocCommentHighlighter(referenceName.Length);
    if (nameHighlighter is { })
    {
      var domainReference = new InvariantDomainReference(referenceName);
      nameHighlighter = nameHighlighter with
      {
        References = new[] { domainReference },
        TextAnimation = UnderlineTextAnimation.Instance
      };
    }

    const string referenceToInvariantText = "Reference to invariant: ";
    var name = new HighlightedText(referenceToInvariantText, TryGetDocCommentHighlighter(referenceToInvariantText.Length));
    name.Add(new HighlightedText(referenceName, nameHighlighter));

    var referenceContentSegment = new InlineReferenceContentSegment(name, description);
    var comment = new InlineReferenceComment(referenceContentSegment, node.GetDocumentRange());

    return new CommentCreationResult(comment, new[] { node });
  }

  public abstract InlineReferenceCommentInfo? TryExtractInlineReferenceInfo([NotNull] ITreeNode node);
  public abstract InlineReferenceCommentInfo? TryExtractCompletionInlineReferenceInfo(
    [NotNull] ITreeNode node, DocumentOffset contextCaretDocumentOffset);

  public IEnumerable<ReferenceInFileDescriptor> FindReferencesToInvariant(string invariantName, ITreeNode node)
  {
    if (TryExtractInlineReferenceInfo(node) is not { } info ||
        node.GetSourceFile() is not { } sourceFile ||
        info.InvariantName != invariantName)
    {
      return EmptyList<ReferenceInFileDescriptor>.Enumerable;
    }

    return new[] { new ReferenceInFileDescriptor(sourceFile, info.InvariantNameOffset) };
  }
}

public record struct InlineReferenceCommentInfo(
  [NotNull] string InvariantName, 
  [CanBeNull] string Description,
  DocumentOffset InvariantNameOffset
);