using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.Rider.Model;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public static class CodeHighlightingKeys
{
  [NotNull] public static Key<string> SandboxDocumentId { get; } = new(nameof(SandboxDocumentId));
  [NotNull] public static Key<IDocument> OriginalDocument { get; } = new(nameof(OriginalDocument));
}

public record CodeHighlightingContext(IHighlightedText Text, IUserDataHolder AdditionalData);