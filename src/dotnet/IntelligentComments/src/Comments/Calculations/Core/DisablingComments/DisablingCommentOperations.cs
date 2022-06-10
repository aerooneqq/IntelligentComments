using System.Collections.Generic;
using System.Linq;
using IntelligentComments.Comments.Domain.Core;
using IntelligentComments.Comments.Domain.Impl;
using IntelligentComments.Comments.Domain.Impl.Content;
using JetBrains.Annotations;
using JetBrains.Application.UI.Icons.CommonThemedIcons;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;

namespace IntelligentComments.Comments.Calculations.Core.DisablingComments;

public record struct InspectionDisablingCommentDto([NotNull] [ItemNotNull] IEnumerable<string> InspectionNames);

public abstract class DisablingCommentOperations : ICommentFromNodeOperations
{
  public int Priority => CommentFromNodeOperationsPriorities.DisablingComment;

  
  [CanBeNull]
  public virtual CommentCreationResult? TryCreate(ITreeNode node)
  {
    if (TryGetDisablingCommentDto(node, out var commentNode) is not { } inspectionDisablingComment || 
        commentNode is null)
    {
      return null;
    }

    var text = CreateDisablingCommentStartingText(node);
    foreach (var name in ExtractNamesFrom(inspectionDisablingComment, node))
    {
      text.Add(name);
      text.Add(new HighlightedText(" ", TryGetDocCommentHighlighter(1, node)));
    }

    var segment = new TextContentSegment(text);
    var range = node.GetDocumentRange();
    var comment = new InspectionDisablingComment(segment, range);
    
    return new CommentCreationResult(comment, new [] { commentNode });
  }

  [NotNull]
  private IHighlightedText CreateDisablingCommentStartingText(ITreeNode contextNode)
  {
    const string disabledInspectionsText = "Disabled inspections: ";
    var highlighter = TryGetDocCommentHighlighter(disabledInspectionsText.Length, contextNode);
    return new HighlightedText(disabledInspectionsText, highlighter);
  }

  [CanBeNull]
  private static TextHighlighter TryGetDocCommentHighlighter(int length, [NotNull] ITreeNode contextNode)
  {
    var provider = LanguageManager.Instance.GetService<IHighlightersProvider>(contextNode.Language);
    return provider.TryGetReSharperHighlighter(DefaultLanguageAttributeIds.DOC_COMMENT, length);
  }
  
  private IEnumerable<IHighlightedText> ExtractNamesFrom(InspectionDisablingCommentDto dto, [NotNull] ITreeNode contextNode)
  {
    var highlightingSettingsManager = contextNode.GetSolution().GetComponent<IHighlightingSettingsManager>();
    return dto.InspectionNames.Select(name =>
    {
      var severityItem = highlightingSettingsManager.GetSeverityItem(name);
      var text = severityItem.Succeed switch
      {
        false => name,
        _ => severityItem.Value.CompoundItemName
      };

      text ??= name;
      var highlighter = TryGetDocCommentHighlighter(text.Length, contextNode);
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
  }

  public IEnumerable<CommentErrorHighlighting> FindErrors(ITreeNode node) => EmptyList<CommentErrorHighlighting>.Enumerable;

  [CanBeNull]
  private static InspectionDisablingCommentDto? TryGetDisablingCommentDto(
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