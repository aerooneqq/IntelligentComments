using System.Linq;
using IntelligentComments.Comments.Calculations;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Calculations.Core.MultilineComments;
using IntelligentComments.Comments.Domain.Core;
using IntelligentComments.Comments.Domain.Impl;
using IntelligentComments.Comments.Domain.Impl.Content;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;

namespace IntelligentComments.Comments.Languages.CSharp.Calculations;

[Language(typeof(CSharpLanguage))]
public class CSharpMultilineCommentBuilder : MultilineCommentBuilderBase
{
  protected override IMultilineComment TryCreateInternal(ITreeNode node)
  {
    if (node is not ICSharpCommentNode { CommentType: CommentType.MULTILINE_COMMENT } commentNode) return null;
    if (!CSharpGroupOfLineCommentsOperations.CheckNoCodeOnTheSameLineWithComment(commentNode)) return null;
    
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