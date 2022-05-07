using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Domain.Core;
using JetBrains.Annotations;

namespace IntelligentComments.Comments.Calculations.Core.MultilineComments.ToDoComments;


public interface IInlineToDoCommentOperations : ICommentFromNodeOperations, INamedEntitiesCommonFinder
{
}

public abstract class InlineToDoCommentOperations : GroupOfLinesLikeCommentOperations, IInlineToDoCommentOperations
{
  [NotNull]
  [ItemNotNull]
  private static readonly string[] ourToDoPrefixes = { "ToDo", "todo", "Todo", "TODO", "To-do", "To do", "To Do", "to-do" };

  protected sealed override string Pattern => $"[ ]*({string.Join("|", ourToDoPrefixes)}): .*";
  protected sealed override string PatternWithName => @$"[ ]*({string.Join("|", ourToDoPrefixes)}) \(name: .+\): .*";
  protected override NameKind NameKind => NameKind.Todo;

  
  protected sealed override TextHighlighter TryGetHighlighter(IHighlightersProvider provider, int length)
  {
    return provider.GetToDoHighlighter(0, length) with { TextAnimation = null };
  }
}