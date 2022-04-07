using System;
using System.Text.RegularExpressions;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.Languages.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpInlineReferenceCommentCreator : InlineReferenceCommentCreator
{
  public override InlineReferenceCommentInfo? TryExtractInlineReferenceInfo(ITreeNode node)
  {
    if (node is not ICSharpCommentNode commentNode ||
        CSharpInlineReferenceCommentsUtil.TryExtractInlineReferenceCommentInfo(commentNode) is not { } info)
    {
      return null;
    }

    return info;
  }

  public override InlineReferenceCommentInfo? TryExtractCompletionInlineReferenceInfo(
    ITreeNode node,
    DocumentOffset caretOffset)
  {
    if (node is not ICSharpCommentNode commentNode ||
        CSharpInlineReferenceCommentsUtil.TryExtractCompletionInlineReferenceCommentInfo(commentNode, caretOffset) is not { } info)
    {
      return null;
    }

    return info;
  }
}

internal static class CSharpInlineReferenceCommentsUtil
{
  [NotNull] private static readonly string ourPossibleNamedEntityNames =
    string.Join("|", DocCommentsBuilderUtil.PossibleReferenceTagSourceAttributes);
  
  [NotNull] private static readonly string ourPattern = $@"[ ]*reference[ ]+to[ ]+({ourPossibleNamedEntityNames}):[ ]+[a-zA-Z\-0-9:]+";
  [NotNull] private static readonly string ourPatternForCompletion = $@"[ ]*reference[ ]+to[ ]+({ourPossibleNamedEntityNames}):[ ]+";


  internal static InlineReferenceCommentInfo? TryExtractCompletionInlineReferenceCommentInfo(
    [NotNull] ICSharpCommentNode commentNode,
    DocumentOffset contextCaretDocumentOffset)
  {
    if (TryGetCommentText(commentNode) is not { } text) return null;
    if (Regex.Matches(text, ourPatternForCompletion).Count != 1) return null;
    if (TryFindReferenceSourceName(text) is not var (nameKind, foundReferenceSourceName)) return null;

    foundReferenceSourceName += ": ";
    var startOfNameIndex = text.IndexOf(foundReferenceSourceName, StringComparison.Ordinal) + foundReferenceSourceName.Length;
    //+2, cz comment starts with "//"
    var offset = commentNode.GetDocumentStartOffset().Shift(startOfNameIndex).Shift(2);
    if (contextCaretDocumentOffset < offset) return null;
    
    var invariantNameEndIndex = text.IndexOf(' ', startOfNameIndex);
    var invariantName = invariantNameEndIndex switch
    {
      > 0 => text.Substring(startOfNameIndex, invariantNameEndIndex - startOfNameIndex),
      _ => text[startOfNameIndex..]
    };

    if (invariantNameEndIndex != -1 && contextCaretDocumentOffset > offset.Shift(invariantName.Length))
      return null;
    
    return new InlineReferenceCommentInfo(invariantName, nameKind, null, offset);
  }

  private record struct FoundReferenceSourceName(NameKind NameKind, string ReferenceSourceName);

  private static FoundReferenceSourceName? TryFindReferenceSourceName([NotNull] string text)
  {
    NameKind? nameKind = null;
    string foundReferenceSourceName = null;
    foreach (var referenceSourceName in DocCommentsBuilderUtil.PossibleReferenceTagSourceAttributes)
    {
      if (text.IndexOf(referenceSourceName, StringComparison.Ordinal) != -1)
      {
        foundReferenceSourceName = referenceSourceName;
        nameKind = DocCommentsBuilderUtil.GetNameKind(referenceSourceName);
        break;
      }
    }

    if (foundReferenceSourceName is null || !nameKind.HasValue) return null;

    return new FoundReferenceSourceName(nameKind.Value, foundReferenceSourceName);
  }

  private static string TryGetCommentText([NotNull] ICSharpCommentNode commentNode)
  {
    if (commentNode is not { CommentType: CommentType.END_OF_LINE_COMMENT }) return null;
    
    return commentNode.CommentText;
  }
  
  internal static InlineReferenceCommentInfo? TryExtractInlineReferenceCommentInfo(
    [NotNull] ICSharpCommentNode commentNode)
  {
    if (TryGetCommentText(commentNode) is not { } text) return null;
    
    var matches = Regex.Matches(text, ourPattern);
    if (matches.Count != 1 || matches[0].Index != 0 || !matches[0].Success) return null;
    if (TryFindReferenceSourceName(text) is not var (nameKind, foundReferenceSourceName)) return null;

    foundReferenceSourceName += ": ";
    var namedEntityIndex = text.IndexOf(foundReferenceSourceName, StringComparison.Ordinal);
    var namedEntityStartIndex = namedEntityIndex + foundReferenceSourceName.Length;
    var namedEntityNameEndIndex = text.IndexOf(' ', namedEntityStartIndex);
    var namedEntityName = namedEntityNameEndIndex switch
    {
      > 0 => text.Substring(namedEntityStartIndex, namedEntityNameEndIndex - namedEntityStartIndex),
      _ => text[namedEntityStartIndex..]
    };

    var description = namedEntityNameEndIndex switch
    {
      > 0 => text[(namedEntityNameEndIndex + 1)..],
      _ => null
    };
    
    //+2, cz comment starts with "//"
    var invariantNameOffset = commentNode.GetDocumentStartOffset().Shift(namedEntityStartIndex).Shift(2);
    return new InlineReferenceCommentInfo(namedEntityName, nameKind, description, invariantNameOffset);
  }
}