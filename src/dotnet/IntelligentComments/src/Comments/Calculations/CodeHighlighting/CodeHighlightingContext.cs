using IntelligentComments.Comments.Domain.Core;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.Util;

namespace IntelligentComments.Comments.Calculations.CodeHighlighting;

public static class CodeHighlightingKeys
{
  [NotNull] public static Key<string> SandboxDocumentId { get; } = new(nameof(SandboxDocumentId));
  [NotNull] public static Key<IDocument> OriginalDocument { get; } = new(nameof(OriginalDocument));
}

public record CodeHighlightingContext([NotNull] IHighlightedText Text, [NotNull] IUserDataHolder AdditionalData);