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
  

  protected override ICommentBase CreateComment(
    IGroupOfLineComments originalComment, IHighlightersProvider provider, string text, string name)
  {
    var highlighter = provider.TryGetDocCommentHighlighter(text.Length) with { TextAnimation = null };
    var toDoHighlightedText = new HighlightedText(text, highlighter);
    var segments = new ContentSegments(new List<IContentSegment>() { new InlineInvariantContentSegment(toDoHighlightedText) });
    var segment = new InvariantContentSegment(null, new EntityWithContentSegments(segments));
    var nameText = name is null ? null : new HighlightedText(name);
    
    return new InlineInvariantComment(nameText, segment, originalComment.Range);  
  }
}