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


public interface IToDoCommentCreator : ICommentFromNodeCreator
{
}

public abstract class ToDoCommentCreator : IToDoCommentCreator
{
  [NotNull]
  [ItemNotNull]
  private static readonly string[] ourToDoPrefixes = { "ToDo", "todo", "Todo", "TODO", "To-do", "To do", "To Do", "to-do" };
  
  [NotNull] 
  private static readonly string ourPattern = $"[ ]*({string.Join("|", ourToDoPrefixes)}): .*";
  
  
  [NotNull] private readonly ILanguageManager myLanguageManager;

  
  protected ToDoCommentCreator()
  {
    myLanguageManager = LanguageManager.Instance;
  }


  public CommentCreationResult? TryCreate(ITreeNode node)
  {
    if (myLanguageManager.TryGetService<IGroupOfLineCommentsCreator>(node.Language) is not { } builder) return null;
    if (builder.TryCreate(node) is not { Comment: IGroupOfLineComments groupOfLineComments } buildResult) 
      return null;

    var text = groupOfLineComments.Text.Text.Text;
    var matches = Regex.Matches(text, ourPattern);
    if (matches.Count != 1 || !matches[0].Success || matches[0].Index != 0) return null;

    var toDoText = text[(text.IndexOf(":", StringComparison.Ordinal) + 2)..];
    var provider = myLanguageManager.GetService<IHighlightersProvider>(node.Language);
    var highlighter = provider.GetToDoHighlighter(0, toDoText.Length) with { TextAnimation = null };
    var toDoHighlightedText = new HighlightedText(toDoText, highlighter);
    var segments = new ContentSegments(new List<IContentSegment>() { new ToDoTextContentSegment(toDoHighlightedText) });
    var segment = new ToDoContentSegment(new EntityWithContentSegments(segments));
    var toDoComment = new ToDoComment(segment, groupOfLineComments.Range);

    return buildResult with { Comment = toDoComment };
  }
}