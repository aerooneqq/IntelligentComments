using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Errors;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments;

public abstract class GroupOfLinesLikeCommentOperations : ISpecialGroupOfLinesCommentsOperations, INamedEntitiesCommonFinder
{
  //todo: bad, but ok for now
  [NotNull] protected abstract string Pattern { get; }
  [NotNull] protected abstract string PatternWithName { get; }
  protected abstract NameKind NameKind { get; }


  public int Priority => CommentFromNodeOperationsPriorities.Default;


  public CommentCreationResult? TryCreate(ITreeNode node)
  {
    if (TryCreateGroupOfLinesCommentsNoMerge(node) is not var (buildResult, groupOfLineComments)) return null;

    var text = GetGroupOfLinesCommentsText(groupOfLineComments);
    var provider = LanguageManager.Instance.GetService<IHighlightersProvider>(node.Language);

    if (CheckMatches(Regex.Matches(text, PatternWithName)))
    {
      var (_, name) = ExtractName(text);
      text = text[(text.IndexOf("):", StringComparison.Ordinal) + 3)..];

      return buildResult with { Comment = CreateComment(groupOfLineComments, provider, text, name) };
    }
    
    if (!CheckMatches(Regex.Matches(text, Pattern))) return null;
    
    text = text[(text.IndexOf(":", StringComparison.Ordinal) + 2)..];
    return buildResult with { Comment = CreateComment(groupOfLineComments, provider, text, null) };
  }

  public IEnumerable<CommentErrorHighlighting> FindErrors(ITreeNode node) => EmptyList<CommentErrorHighlighting>.Enumerable;

  public bool CanBeStartOfSpecialGroupOfLineComments(ITreeNode node)
  {
    if (node is not ICommentNode commentNode) return false;

    return CheckMatches(Regex.Matches(commentNode.CommentText, Pattern)) ||
           CheckMatches(Regex.Matches(commentNode.CommentText, PatternWithName));
  }

  [NotNull] 
  private static string GetGroupOfLinesCommentsText([NotNull] IGroupOfLineComments comments) => comments.Text.Text.Text;

  private static (CommentCreationResult, IGroupOfLineComments)? TryCreateGroupOfLinesCommentsNoMerge([NotNull] ITreeNode node)
  {
    if (LanguageManager.Instance.TryGetService<IGroupOfLineCommentsOperations>(node.Language) is not { } builder) return null;
    if (builder.TryCreateNoMerge(node) is not { Comment: IGroupOfLineComments groupOfLineComments } buildResult) 
      return null;

    return (buildResult, groupOfLineComments);
  }

  private static bool CheckMatches([NotNull] MatchCollection matches) => matches.Count == 1 && matches[0].Success && matches[0].Index == 0;


  protected record struct NameExtraction(int Index, [NotNull] string Name);
  
  [NotNull]
  protected virtual NameExtraction ExtractName([NotNull] string text)
  {
    const string name = "name:";
    var index = text.IndexOf(name, StringComparison.Ordinal) + name.Length + 1;
    text = text[index..text.IndexOf(")", StringComparison.Ordinal)];
    return new NameExtraction(index, text);
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
    var nameText = name is null ? null : new HighlightedText(name);
    var segments = new ContentSegments(new List<IContentSegment> { new InlineContentSegment(nameText, toDoHighlightedText, NameKind) });
    var content = new EntityWithContentSegments(segments);

    return NameKind switch
    {
      NameKind.Hack => new InlineHackComment(content, originalComment.Range),
      NameKind.Invariant => new InlineInvariantComment(content, originalComment.Range),
      NameKind.Todo => new InlineToDoComment(content, originalComment.Range),
      _ => throw new ArgumentOutOfRangeException(NameKind.ToString())
    };
  }

  protected abstract TextHighlighter TryGetHighlighter([NotNull] IHighlightersProvider provider, int length);

  public IEnumerable<CommonNamedEntityDescriptor> FindReferences(ITreeNode node, NameWithKind nameWithKind)
  {
    return EmptyList<CommonNamedEntityDescriptor>.Enumerable;
  }

  public IEnumerable<CommonNamedEntityDescriptor> FindAllReferences(ITreeNode node)
  {
    return EmptyList<CommonNamedEntityDescriptor>.Enumerable;
  }

  public IEnumerable<CommonNamedEntityDescriptor> FindNames(ITreeNode node)
  {
    if (TryCreateGroupOfLinesCommentsNoMerge(node) is not var (_, groupOfLineComments))
      return EmptyList<CommonNamedEntityDescriptor>.Enumerable;

    var text = GetGroupOfLinesCommentsText(groupOfLineComments);
    var matches = Regex.Matches(text, PatternWithName);
    
    if (CheckMatches(matches) && node.GetSourceFile() is { } sourceFile)
    {
      var (index, name) = ExtractName(text);
      
      //+2 cz comments starts with //
      var nameRange = groupOfLineComments.Range.StartOffset.Shift(2).Shift(index).ExtendRight(name.Length);
      
      return new CommonNamedEntityDescriptor[] { new(sourceFile, nameRange, new NameWithKind(name, NameKind)) };
    }
    
    return EmptyList<CommonNamedEntityDescriptor>.Enumerable;
  }
}