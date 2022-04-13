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

public interface IInlineHackCommentCreator : ICommentFromNodeCreator, INamesInCommentFinder
{
}

public abstract class InlineHackCommentCreator : GroupOfLinesLikeCommentCreator, IInlineHackCommentCreator
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
