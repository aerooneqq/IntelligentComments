using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;

public interface IMultilineCommentsBuilder
{
  [NotNull] IMultilineComment Build();
}

public class MultilineCommentsBuilder : IMultilineCommentsBuilder
{
  [NotNull] private const string Star = "*";
  
  [NotNull] private readonly ICSharpCommentNode myCommentNode;
  [NotNull] private readonly IHighlightersProvider myHighlightersProvider;
  [NotNull] private readonly string myCommentAttributeId;


  public MultilineCommentsBuilder([NotNull] ICSharpCommentNode commentNode)
  {
    myCommentNode = commentNode;
    myHighlightersProvider = LanguageManager.Instance.GetService<IHighlightersProvider>(commentNode.Language);
    myCommentAttributeId = DefaultLanguageAttributeIds.DOC_COMMENT;
  }
  
  
  public IMultilineComment Build()
  {
    var text = CommentsBuilderUtil.PreprocessText(myCommentNode.CommentText, null);
    text = text.Split('\n').Select(line =>
    {
      if (line.StartsWith(Star))
      {
        line = line[1..];
      }

      return CommentsBuilderUtil.PreprocessText(line, null);
    }).Join("\n");

    text = CommentsBuilderUtil.PreprocessText(text, null);
    var highlighter = myHighlightersProvider.TryGetReSharperHighlighter(myCommentAttributeId, text.Length);
    var highlightedText = new HighlightedText(text, highlighter);
    var textSegment = new TextContentSegment(highlightedText);
    var range = myCommentNode.GetDocumentRange();

    return new MultilineComment(textSegment, range);
  }
}