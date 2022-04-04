using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments.ToDoComments;

public record struct ToDoCommentBuildResult([NotNull] IToDoComment Comment, [NotNull] IEnumerable<ITreeNode> CommentNodes);

public interface IToDoCommentBuilder
{
  [CanBeNull] 
  ToDoCommentBuildResult? TryBuild([NotNull] ITreeNode node);
}

public abstract class ToDoCommentBuilder : IToDoCommentBuilder
{
  [NotNull]
  [ItemNotNull]
  private static readonly string[] ourToDoPrefixes = { "ToDo", "todo", "Todo", "TODO", "To-do", "To do", "To Do", "to-do" };
  
  [NotNull] 
  private static readonly string ourPattern = $"[ ]*({string.Join("|", ourToDoPrefixes)}): .*";
  
  
  [NotNull] private readonly ILanguageManager myLanguageManager;

  
  protected ToDoCommentBuilder()
  {
    myLanguageManager = LanguageManager.Instance;
  }


  public ToDoCommentBuildResult? TryBuild(ITreeNode node)
  {
    if (myLanguageManager.TryGetService<IGroupOfLineCommentsBuilder>(node.Language) is not { } builder) return null;
    if (builder.Build(node) is not { } groupOfLineCommentsBuildResult) return null;

    var text = groupOfLineCommentsBuildResult.GroupOfLineComments.Text.Text.Text;
    var matches = Regex.Matches(text, ourPattern);
    if (matches.Count != 1 || !matches[0].Success || matches[0].Index != 0) return null;

    var toDoText = text[(text.IndexOf(":", StringComparison.Ordinal) + 2)..];
    var provider = myLanguageManager.GetService<IHighlightersProvider>(node.Language);
    var highlighter = provider.GetToDoHighlighter(0, toDoText.Length) with { TextAnimation = null };
    var toDoHighlightedText = new HighlightedText(toDoText, highlighter);
    var segments = new ContentSegments(new List<IContentSegment>() { new ToDoTextContentSegment(toDoHighlightedText) });
    var contentSegment = new EntityWithContentSegments(segments);
    
    var segment = new ToDoContentSegment(new ToDo(contentSegment, EmptyList<IDomainReference>.Enumerable));
    var toDoComment = new ToDoComment(segment, groupOfLineCommentsBuildResult.GroupOfLineComments.Range);

    return new ToDoCommentBuildResult(toDoComment, groupOfLineCommentsBuildResult.CommentNodes);
  }
}