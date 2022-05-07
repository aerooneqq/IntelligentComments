using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Domain.Core;
using JetBrains.Annotations;

namespace IntelligentComments.Comments.Calculations.Core.MultilineComments.Invariants;

public interface IInlineInvariantCommentOperations : ICommentFromNodeOperations, INamedEntitiesCommonFinder
{
  
}

public abstract class InlineInvariantCommentOperations : GroupOfLinesLikeCommentOperations, IInlineInvariantCommentOperations
{
  [NotNull] [ItemNotNull] 
  private static readonly string[] ourInvariantPrefixes = { "Invariant", "invariant" };
  
  protected sealed override string Pattern => $"[ ]*({string.Join("|", ourInvariantPrefixes)}): .*";
  protected override string PatternWithName => @$"[ ]*({string.Join("|", ourInvariantPrefixes)}) \(name: .+\): .*";
  protected override NameKind NameKind => NameKind.Invariant;
  
  
  protected sealed override TextHighlighter TryGetHighlighter(IHighlightersProvider provider, int length)
  {
    if (provider.TryGetDocCommentHighlighter(length) is not { } highlighter) return null;
    return highlighter with { TextAnimation = null };
  }
}