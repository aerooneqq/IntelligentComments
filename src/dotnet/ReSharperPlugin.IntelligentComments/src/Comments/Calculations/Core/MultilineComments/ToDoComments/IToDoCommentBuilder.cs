using System;
using System.Text.RegularExpressions;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments.ToDoComments;

public interface IToDoCommentBuilder
{
  [CanBeNull] CommentProcessingResult TryBuild([NotNull] ITreeNode node);
}

public abstract class ToDoCommentBuilder : IToDoCommentBuilder
{
  [ItemNotNull] [NotNull] 
  private static readonly string[] ourToDoPrefixes = { "ToDo", "todo", "Todo", "TODO", "To-do", "To do", "To Do", "to-do" };
  
  [NotNull] 
  private static readonly string ourPattern = $"[ ]*({string.Join("|", ourToDoPrefixes)}): .*";
  
  
  [NotNull] private readonly ILanguageManager myLanguageManager;

  
  protected ToDoCommentBuilder()
  {
    myLanguageManager = LanguageManager.Instance;
  }


  public CommentProcessingResult TryBuild(ITreeNode node)
  {
    if (myLanguageManager.TryGetService<IGroupOfLineCommentsBuilder>(node.Language) is not { } builder) return null;
    if (builder.Build(node) is not { } groupOfLineCommentsBuildResult) return null;

    var text = groupOfLineCommentsBuildResult.GroupOfLineComments.Text.Text.Text;
    var matches = Regex.Matches(text, ourPattern);
    if (matches.Count != 1 || !matches[0].Success || matches[0].Index != 0) return null;

    var toDoText = text[(text.IndexOf(":", StringComparison.Ordinal) + 2)..];
    toDoText = "Todo: " + toDoText;

    var provider = myLanguageManager.GetService<IHighlightersProvider>(node.Language);
    var highlighter = provider.GetToDoHighlighter(0, toDoText.Length);
    var toDoHighlightedText = new HighlightedText(toDoText, highlighter);
    var segment = new ToDoContentSegment(new ToDo(toDoHighlightedText, EmptyList<IDomainReference>.Enumerable));
    var toDoComment = new ToDoComment(segment, groupOfLineCommentsBuildResult.GroupOfLineComments.Range);

    return CommentProcessingResult.CreateSuccess(toDoComment);
  }
}