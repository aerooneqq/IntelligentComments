using System;
using System.Text.RegularExpressions;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors.CSharp;

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
  private const string Pattern = @"[ ]*reference[ ]+to[ ]+invariant:[ ]+[a-zA-Z\-0-9:]+";
  private const string PatternForCompletion = @"[ ]*reference[ ]+to[ ]+invariant:[ ]+";
  private const string InvariantKey = "invariant: ";

  
  internal static InlineReferenceCommentInfo? TryExtractCompletionInlineReferenceCommentInfo(
    [NotNull] ICSharpCommentNode commentNode, DocumentOffset contextCaretDocumentOffset)
  {
    if (TryGetCommentText(commentNode) is not { } text) return null;
    if (Regex.Matches(text, PatternForCompletion).Count != 1) return null;

    var startOfNameIndex = text.IndexOf(InvariantKey, StringComparison.Ordinal) + InvariantKey.Length;
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
    
    return new InlineReferenceCommentInfo(invariantName, null, offset);
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
    
    var matches = Regex.Matches(text, Pattern);
    if (matches.Count != 1) return null;

    var invariantKeyIndex = text.IndexOf(InvariantKey, StringComparison.Ordinal);
    var invariantNameStartIndex = invariantKeyIndex + InvariantKey.Length;
    var invariantNameEndIndex = text.IndexOf(' ', invariantNameStartIndex);
    var invariantName = invariantNameEndIndex switch
    {
      > 0 => text.Substring(invariantNameStartIndex, invariantNameEndIndex - invariantNameStartIndex),
      _ => text[invariantNameStartIndex..]
    };

    var description = invariantNameEndIndex switch
    {
      > 0 => text[(invariantNameEndIndex + 1)..],
      _ => null
    };
    
    //+2, cz comment starts with "//"
    var invariantNameOffset = commentNode.GetDocumentStartOffset().Shift(invariantNameStartIndex).Shift(2);
    return new InlineReferenceCommentInfo(invariantName, description, invariantNameOffset);
  }
}