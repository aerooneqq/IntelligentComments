using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DisablingComments;

public record struct InspectionDisablingCommentDto(IEnumerable<string> InspectionNames);

public class DisablingCommentCreator
{
  [CanBeNull]
  public virtual CommentProcessingResult TryCreate([NotNull] ITreeNode node)
  {
    if (TryGetDisablingCommentDto(node, out var commentNode) is not { } inspectionDisablingComment || 
        commentNode is null)
    {
      return null;
    }
    
    var highlightingSettingsManager = node.GetSolution().GetComponent<IHighlightingSettingsManager>();
    var provider = LanguageManager.Instance.GetService<IHighlightersProvider>(node.Language);

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
    var range = node.GetDocumentRange();
    var comment = new InspectionDisablingComment(segment, range);
    var result = CommentProcessingResult.CreateSuccess(comment);
    
    return result;
  }
  
  [CanBeNull]
  private InspectionDisablingCommentDto? TryGetDisablingCommentDto(
    [CanBeNull] ITreeNode element,
    [CanBeNull] out ICSharpCommentNode cSharpCommentNode)
  {
    cSharpCommentNode = null;

    if (element is ICSharpDocCommentBlock ||
        element is not ICSharpCommentNode cSharpComment || 
        TryGetInspectionDisablingCommentDto(cSharpComment) is not { } inspectionDisablingComment)
    {
      return null;
    }
    
    cSharpCommentNode = cSharpComment;
    return inspectionDisablingComment;
  }

  [CanBeNull]
  private static InspectionDisablingCommentDto? TryGetInspectionDisablingCommentDto(
    [NotNull] ICSharpCommentNode commentNode)
  {
    var constructInfo = ReSharperControlConstruct.ParseCommentText(commentNode.CommentText);
    if (constructInfo.IsRecognized && constructInfo.IsDisable)
    {
      return new InspectionDisablingCommentDto(constructInfo.GetControlIds().ToList());
    }

    return null;
  }
}