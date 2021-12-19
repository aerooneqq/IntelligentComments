using JetBrains.Annotations;
using JetBrains.ProjectModel;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public interface IHighlightersProvider
{
  [NotNull] TextHighlighter GetCXmlElementHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetParamRefElementHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeAlsoLinkHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeAlsoMemberHighlighter(int startOffset, int endOffset);
}
  
  
[SolutionComponent]
public class HighlightersProvider : IHighlightersProvider
{
  [NotNull] private static readonly TextHighlighterAttributes ourDefaultAttributes = new(FontStyle.Regular, true, 400);
  
  
  [NotNull] private const string CElementKey = "doc.comment.c.element.text";
  [NotNull] private const string ParamRefKey = "doc.comment.param.ref.text";
  [NotNull] private const string SeeAlsoLinkKey = "see.also.link.text";
  [NotNull] private const string SeeAlsoMemberKey = "see.also.member.text";


  public TextHighlighter GetCXmlElementHighlighter(int startOffset, int endOffset) =>
    new(CElementKey, startOffset, endOffset, ourDefaultAttributes);

  public TextHighlighter GetParamRefElementHighlighter(int startOffset, int endOffset) =>
    new(ParamRefKey, startOffset, endOffset, ourDefaultAttributes);

  public TextHighlighter GetSeeAlsoLinkHighlighter(int startOffset, int endOffset) =>
    new(SeeAlsoLinkKey, startOffset, endOffset, ourDefaultAttributes);

  public TextHighlighter GetSeeAlsoMemberHighlighter(int startOffset, int endOffset) =>
    new(SeeAlsoMemberKey, startOffset, endOffset, ourDefaultAttributes);
}