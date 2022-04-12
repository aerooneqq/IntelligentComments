using System;
using System.Collections.Generic;
using JetBrains.Annotations;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments.ToDoComments;


public interface IToDoCommentCreator : ICommentFromNodeCreator, INamesInCommentFinder
{
}

public abstract class ToDoCommentCreator : GroupOfLinesLikeCommentCreator, IToDoCommentCreator
{
  [NotNull]
  [ItemNotNull]
  private static readonly string[] ourToDoPrefixes = { "ToDo", "todo", "Todo", "TODO", "To-do", "To do", "To Do", "to-do" };

  protected sealed override string Pattern => $"[ ]*({string.Join("|", ourToDoPrefixes)}): .*";
  protected sealed override string PatternWithName => @$"[ ]*({string.Join("|", ourToDoPrefixes)}) \(name: .+\): .*";
  protected override NameKind NameKind => NameKind.Todo;

  
  protected sealed override ICommentBase CreateComment(
    IGroupOfLineComments originalComment, IHighlightersProvider provider, string text, string name)
  {
    var highlighter = provider.GetToDoHighlighter(0, text.Length) with { TextAnimation = null };
    var toDoHighlightedText = new HighlightedText(text, highlighter);
    var segments = new ContentSegments(new List<IContentSegment>() { new ToDoTextContentSegment(toDoHighlightedText) });
    var segment = new ToDoContentSegment(null, new EntityWithContentSegments(segments));
    var nameText = name is null ? null : new HighlightedText(name);
    var toDoComment = new InlineToDoComment(nameText, segment, originalComment.Range);
    
    return toDoComment;
  }
}