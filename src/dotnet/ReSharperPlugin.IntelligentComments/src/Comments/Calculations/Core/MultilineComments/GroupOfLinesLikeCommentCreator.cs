using System;
using System.Text.RegularExpressions;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments;

public abstract class GroupOfLinesLikeCommentCreator : ICommentFromNodeCreator
{
  [NotNull] protected readonly ILanguageManager LanguageManager;
  [NotNull] protected abstract string Pattern { get; }


  protected GroupOfLinesLikeCommentCreator()
  {
    LanguageManager = JetBrains.ReSharper.Psi.LanguageManager.Instance;
  }

  
  public CommentCreationResult? TryCreate(ITreeNode node)
  {
    if (LanguageManager.TryGetService<IGroupOfLineCommentsCreator>(node.Language) is not { } builder) return null;
    if (builder.TryCreate(node) is not { Comment: IGroupOfLineComments groupOfLineComments } buildResult) 
      return null;

    var text = groupOfLineComments.Text.Text.Text;
    var matches = Regex.Matches(text, Pattern);
    if (matches.Count != 1 || !matches[0].Success || matches[0].Index != 0) return null;

    text = text[(text.IndexOf(":", StringComparison.Ordinal) + 2)..];
    var provider = LanguageManager.GetService<IHighlightersProvider>(node.Language);

    return buildResult with { Comment = CreateComment(groupOfLineComments, provider, text) };
  }

  protected abstract ICommentBase CreateComment(
    [NotNull] IGroupOfLineComments originalComment, [NotNull] IHighlightersProvider provider, [NotNull] string text);
}