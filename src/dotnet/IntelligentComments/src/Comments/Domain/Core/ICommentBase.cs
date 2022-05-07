using IntelligentComments.Comments.Domain.Core.Content;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.Rd.Base;

namespace IntelligentComments.Comments.Domain.Core;

public interface ICommentBase : IPrintable
{
  DocumentRange Range { get; }
}

public interface IDisablingComment : ICommentBase
{
  [NotNull] ITextContentSegment DisabledInspections { get; }
}

public interface IInlineComment : ICommentBase
{
  [NotNull] IEntityWithContentSegments Content { get; }
}

public interface IInlineToDoComment : IInlineComment
{
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

public interface IInlineReferenceComment : ICommentBase
{
  [NotNull] IInlineReferenceContentSegment Segment { get; }
}

public interface IInlineHackComment : IInlineComment
{
}

public interface IInlineInvariantComment : IInlineComment
{
}