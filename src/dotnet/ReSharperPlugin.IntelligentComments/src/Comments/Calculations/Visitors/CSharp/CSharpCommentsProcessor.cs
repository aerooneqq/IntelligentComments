using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpCommentsProcessor : CommentsProcessorBase
{
  public override void ProcessBeforeInterior(ITreeNode element)
  {
    if (VisitedComments.Contains(element)) return;

    if (element is not ICSharpDocCommentBlock && 
        element is ICSharpCommentNode cSharpComment && 
        TryGetInspectionDisablingCommentDto(cSharpComment) is { } inspectionDisablingComment)
    {
      ProcessDisablingComment(cSharpComment, inspectionDisablingComment);
      return;
    }
    
    switch (element)
    {
      case ICSharpDocCommentBlock docCommentBlock:
      {
        ProcessDocCommentBlock(docCommentBlock);
        break;
      }
      case ICSharpCommentNode { CommentType: CommentType.END_OF_LINE_COMMENT } commentNode:
      {
        ProcessLineComment(commentNode);
        break;
      }
      case ICSharpCommentNode { CommentType: CommentType.MULTILINE_COMMENT } commentNode:
      {
        ProcessMultilineComment(commentNode);
        break;
      }
    }
  }

  private void ProcessDisablingComment(
    [NotNull] ITreeNode commentNode,
    InspectionDisablingCommentDto inspectionDisablingComment)
  {
    VisitedComments.Add(commentNode);
    var highlightingSettingsManager = commentNode.GetSolution().GetComponent<IHighlightingSettingsManager>();
    var provider = LanguageManager.Instance.GetService<IHighlightersProvider>(commentNode.Language);

    TextHighlighter GetHighlighter(int length)
    {
      return provider.TryGetReSharperHighlighter(DefaultLanguageAttributeIds.DOC_COMMENT, length);
    }
    
    var names = inspectionDisablingComment.InspectionNames.Select(name =>
    {
      var severityItem = highlightingSettingsManager.GetSeverityItem(name);
      var text = severityItem.Succeed switch
      {
        false => name,
        _ => severityItem.Value.CompoundItemName
      };

      text ??= name;
      var highlighter = GetHighlighter(text.Length);
      if (highlighter is { })
      {
        highlighter = highlighter with
        {
          Attributes = highlighter.Attributes with
          {
            FontStyle = FontStyle.Italic,
            Underline = true
          }
        };
      }

      return new HighlightedText(text, highlighter);
    });

    const string disabledInspectionsText = "Disabled inspections: ";
    var text = new HighlightedText(disabledInspectionsText, GetHighlighter(disabledInspectionsText.Length));
    foreach (var name in names)
    {
      text.Add(name);
      text.Add(new HighlightedText(" ", GetHighlighter(1)));
    }
    
    var segment = new TextContentSegment(text);
    var range = commentNode.GetDocumentRange();
    var comment = new InspectionDisablingComment(segment, range);
    var result = CommentProcessingResult.CreateWithoutErrors(comment);
    
    Comments.Add(result);
  }

  private void ProcessDocCommentBlock([NotNull] ICSharpDocCommentBlock docCommentBlock)
  {
    VisitedComments.Add(docCommentBlock);

    var errorsCollector = new CSharpCommentProblemsCollector(docCommentBlock);
    if (errorsCollector.Run() is { Count: > 0 } errors)
    {
      var range = docCommentBlock.GetDocumentRange();
      Comments.Add(CommentProcessingResult.CreateWithErrors(errors, CSharpLanguage.Instance!, range));
      return;
    }
    
    var builder = new CSharpDocCommentBuilder(docCommentBlock);

    if (builder.Build() is { } comment)
    {
      Comments.Add(CommentProcessingResult.CreateWithoutErrors(comment));
    }
  }

  private void ProcessLineComment([NotNull] ICSharpCommentNode commentNode)
  {
    var builder = new CSharpGroupOfLineCommentsBuilder(commentNode);

    VisitedComments.Add(commentNode);

    if (builder.Build() is not var (groupOfLineComments, includedCommentsNodes)) return;
    
    Comments.Add(CommentProcessingResult.CreateWithoutErrors(groupOfLineComments));
    VisitedComments.AddRange(includedCommentsNodes);
  }

  private record struct InspectionDisablingCommentDto(IEnumerable<string> InspectionNames);

  [CanBeNull]
  private InspectionDisablingCommentDto? TryGetInspectionDisablingCommentDto([NotNull] ICSharpCommentNode commentNode)
  {
    var constructInfo = ReSharperControlConstruct.ParseCommentText(commentNode.CommentText);
    if (constructInfo.IsRecognized && constructInfo.IsDisable)
    {
      return new InspectionDisablingCommentDto(constructInfo.GetControlIds().ToList());
    }

    return null;
  }

  private void ProcessMultilineComment([NotNull] ICSharpCommentNode commentNode)
  {
    var builder = new CSharpMultilineCommentBuilder(commentNode);
    
    VisitedComments.Add(commentNode);
    
    if (builder.Build() is { } multilineComment)
    {
      Comments.Add(CommentProcessingResult.CreateWithoutErrors(multilineComment));
    }
  }
}