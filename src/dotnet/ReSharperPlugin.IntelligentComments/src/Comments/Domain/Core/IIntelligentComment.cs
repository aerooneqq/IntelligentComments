using JetBrains.Annotations;
using JetBrains.DocumentModel;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

public interface ICommentBase
{
  DocumentRange Range { get; }
  int CreateIdentifier();
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

public interface IIntelligentComment : ICommentBase
{
  [NotNull] IIntelligentCommentContent Content { get; }
}