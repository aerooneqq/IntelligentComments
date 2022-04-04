using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments;

public interface IMultilineCommentsBuilder
{
  [NotNull] IMultilineComment Build([NotNull] ICSharpCommentNode commentNode);
}

public abstract class MultilineCommentBuilderBase : IMultilineCommentsBuilder
{
  [NotNull] private const string Star = "*";


  public IMultilineComment Build([NotNull] ICSharpCommentNode commentNode)
  {
    var highlightersProvider = LanguageManager.Instance.GetService<IHighlightersProvider>(commentNode.Language);
    var text = DocCommentsBuilderUtil.PreprocessText(commentNode.CommentText, null);
    text = text.Split('\n').Select(line =>
    {
      if (line.StartsWith(Star))
      {
        line = line[1..];
      }

      return DocCommentsBuilderUtil.PreprocessText(line, null);
    }).Join("\n");

    text = DocCommentsBuilderUtil.PreprocessText(text, null);
    var highlighter = highlightersProvider.TryGetDocCommentHighlighter(text.Length);
    var highlightedText = new HighlightedText(text, highlighter);
    var textSegment = new TextContentSegment(highlightedText);
    var range = commentNode.GetDocumentRange();

    return new MultilineComment(textSegment, range);
  }
}