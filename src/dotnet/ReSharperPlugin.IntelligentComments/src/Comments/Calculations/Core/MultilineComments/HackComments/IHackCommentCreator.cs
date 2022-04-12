using System;
using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments.HackComments;

public interface IHackCommentCreator : ICommentFromNodeCreator, INamesInCommentFinder
{
}

public abstract class HackCommentCreator : GroupOfLinesLikeCommentCreator, IHackCommentCreator
{
  [NotNull] [ItemNotNull] 
  private static readonly string[] ourHackPrefixes = { "Hack", "hack" };
  
  protected sealed override string Pattern => $"[ ]*({string.Join("|", ourHackPrefixes)}): .*";
  protected override string PatternWithName => @$"[ ]*({string.Join("|", ourHackPrefixes)}) \(name: .+\): .*";
  protected override NameKind NameKind => NameKind.Hack;
  

  protected override ICommentBase CreateComment(
    IGroupOfLineComments originalComment, IHighlightersProvider provider, string text, string name)
  {
    var highlighter = provider.GetHackHighlighter(0, text.Length) with { TextAnimation = null };
    var toDoHighlightedText = new HighlightedText(text, highlighter);
    var segments = new ContentSegments(new List<IContentSegment>() { new InlineHackContentSegment(toDoHighlightedText) });
    var segment = new HackContentSegment(null, new EntityWithContentSegments(segments));
    var nameText = name is null ? null : new HighlightedText(name);
    
    return new InlineHackComment(nameText, segment, originalComment.Range);  
  }
}
