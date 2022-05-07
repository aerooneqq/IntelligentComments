using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Domain.Core;
using JetBrains.Annotations;

namespace IntelligentComments.Comments.Calculations.Core.MultilineComments.HackComments;

public interface IInlineHackCommentOperations : ICommentFromNodeOperations, INamedEntitiesCommonFinder
{
}

public abstract class InlineHackCommentOperations : GroupOfLinesLikeCommentOperations, IInlineHackCommentOperations
{
  [NotNull] [ItemNotNull] 
  private static readonly string[] ourHackPrefixes = { "Hack", "hack" };
  
  protected sealed override string Pattern => $"[ ]*({string.Join("|", ourHackPrefixes)}): .*";
  protected override string PatternWithName => @$"[ ]*({string.Join("|", ourHackPrefixes)}) \(name: .+\): .*";
  protected override NameKind NameKind => NameKind.Hack;
  
  
  protected sealed override TextHighlighter TryGetHighlighter(IHighlightersProvider provider, int length)
  {
    return provider.GetHackHighlighter(0, length) with { TextAnimation = null };
  }
}
