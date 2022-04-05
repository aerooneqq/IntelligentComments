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

public abstract class ToDoCommentCreator : GroupOfLinesLikeCommentCreator, IToDoCommentCreator
{
  [NotNull]
  [ItemNotNull]
  private static readonly string[] ourToDoPrefixes = { "ToDo", "todo", "Todo", "TODO", "To-do", "To do", "To Do", "to-do" };

  protected sealed override string Pattern => $"[ ]*({string.Join("|", ourToDoPrefixes)}): .*";


  protected sealed override ICommentBase CreateComment(
    IGroupOfLineComments originalComment, IHighlightersProvider provider, string text)
  {
    var highlighter = provider.GetToDoHighlighter(0, text.Length) with { TextAnimation = null };
    var toDoHighlightedText = new HighlightedText(text, highlighter);
    var segments = new ContentSegments(new List<IContentSegment>() { new ToDoTextContentSegment(toDoHighlightedText) });
    var segment = new ToDoContentSegment(new EntityWithContentSegments(segments));
    var toDoComment = new ToDoComment(segment, originalComment.Range);
    return toDoComment;
  }
}