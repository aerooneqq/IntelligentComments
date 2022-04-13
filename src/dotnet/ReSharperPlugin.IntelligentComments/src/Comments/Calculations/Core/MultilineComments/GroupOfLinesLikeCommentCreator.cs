using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments;

public abstract class GroupOfLinesLikeCommentCreator : ICommentFromNodeCreator, INamesInCommentFinder
{
  [NotNull] protected readonly ILanguageManager LanguageManager;
  
  //todo: bad, but ok for now
  [NotNull] protected abstract string Pattern { get; }
  [NotNull] protected abstract string PatternWithName { get; }
  protected abstract NameKind NameKind { get; }


  public int Priority => CommentFromNodeCreatorsPriorities.Default;

  
  protected GroupOfLinesLikeCommentCreator()
  {
    LanguageManager = JetBrains.ReSharper.Psi.LanguageManager.Instance;
  }

  
  public CommentCreationResult? TryCreate(ITreeNode node)
  {
    if (TryCreateGroupOfLinesCommentsNoMerge(node) is not var (buildResult, groupOfLineComments)) return null;

    var text = GetGroupOfLinesCommentsText(groupOfLineComments);
    var provider = LanguageManager.GetService<IHighlightersProvider>(node.Language);

    if (CheckMatches(Regex.Matches(text, PatternWithName)))
    {
      var name = ExtractName(text);
      text = text[(text.IndexOf("):", StringComparison.Ordinal) + 3)..];
      return buildResult with { Comment = CreateComment(groupOfLineComments, provider, text, name) };
    }
    
    if (!CheckMatches(Regex.Matches(text, Pattern))) return null;
    
    text = text[(text.IndexOf(":", StringComparison.Ordinal) + 2)..];
    return buildResult with { Comment = CreateComment(groupOfLineComments, provider, text, null) };
  }
  
  [NotNull] 
  private static string GetGroupOfLinesCommentsText([NotNull] IGroupOfLineComments comments) => comments.Text.Text.Text;

  private (CommentCreationResult, IGroupOfLineComments)? TryCreateGroupOfLinesCommentsNoMerge([NotNull] ITreeNode node)
  {
    if (LanguageManager.TryGetService<IGroupOfLineCommentsCreator>(node.Language) is not { } builder) return null;
    if (builder.TryCreateNoMerge(node) is not { Comment: IGroupOfLineComments groupOfLineComments } buildResult) 
      return null;

    return (buildResult, groupOfLineComments);
  }

  private static bool CheckMatches([NotNull] MatchCollection matches) => matches.Count == 1 && matches[0].Success && matches[0].Index == 0;

  [NotNull]
  protected virtual string ExtractName([NotNull] string text)
  {
    const string name = "name:";
    var index = text.IndexOf(name, StringComparison.Ordinal);
    return text[(index + name.Length + 1)..(text.IndexOf(")", StringComparison.Ordinal))];
  }

  [NotNull]
  private ICommentBase CreateComment(
    [NotNull] IGroupOfLineComments originalComment,
    [NotNull] IHighlightersProvider provider,
    [NotNull] string text,
    [CanBeNull] string name)
  {
    var highlighter = TryGetHighlighter(provider, text.Length);
    var toDoHighlightedText = new HighlightedText(text, highlighter);
    var segments = new ContentSegments(new List<IContentSegment> { new InlineContentSegment(toDoHighlightedText, NameKind) });
    var content = new EntityWithContentSegments(segments);
    var nameText = name is null ? null : new HighlightedText(name);
    
    return new InlineToDoComment(nameText, content, originalComment.Range);
  }

  protected abstract TextHighlighter TryGetHighlighter([NotNull] IHighlightersProvider provider, int length);

  public IEnumerable<NameInFileDescriptor> FindNames(ITreeNode node)
  {
    if (TryCreateGroupOfLinesCommentsNoMerge(node) is not var (_, groupOfLineComments))
      return EmptyList<NameInFileDescriptor>.Enumerable;

    var text = GetGroupOfLinesCommentsText(groupOfLineComments);
    if (CheckMatches(Regex.Matches(text, PatternWithName)) && node.GetSourceFile() is { } sourceFile)
    {
      var name = ExtractName(text);
      var offset = groupOfLineComments.Range.StartOffset;
      return new NameInFileDescriptor[] { new(sourceFile, offset, new NameWithKind(name, NameKind)) };
    }
    
    return EmptyList<NameInFileDescriptor>.Enumerable;
  }
}