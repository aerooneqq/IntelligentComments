using System.Collections.Generic;
using JetBrains.Annotations;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments.Invariants;

public interface IInlineInvariantCommentCreator : ICommentFromNodeCreator, INamesInCommentFinder
{
  
}

public abstract class InlineInvariantCommentCreator : GroupOfLinesLikeCommentCreator, IInlineInvariantCommentCreator
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