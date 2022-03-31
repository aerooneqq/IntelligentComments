using JetBrains.Annotations;
using JetBrains.DocumentModel;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

public interface ICommentBase
{
  DocumentRange Range { get; }
}

public interface IDisablingComment : ICommentBase
{
  [NotNull] ITextContentSegment DisabledInspections { get; }
}

public interface IToDoComment : ICommentBase
{
  [NotNull] IToDoContentSegment ToDoContentSegment { get; }
}

public interface IInvalidComment : ICommentBase
{
  [NotNull] ITextContentSegment ErrorsSummary { get; }
}

public interface IGroupOfLineComments : ICommentBase
{
  [NotNull] ITextContentSegment Text { get; }
}

public interface IMultilineComment : ICommentBase
{
  [NotNull] ITextContentSegment Text { get; }
}

public interface IDocComment : ICommentBase
{
  [NotNull] IIntelligentCommentContent Content { get; }
}