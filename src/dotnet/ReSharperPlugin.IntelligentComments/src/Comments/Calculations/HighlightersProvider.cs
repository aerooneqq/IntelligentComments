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
  [NotNull] TextHighlighter GetSeeCodeEntityHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeHttpLinkHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeLangWordHighlighter(int startOffset, int endOffset);
}
  
  
[SolutionComponent]
public class HighlightersProvider : IHighlightersProvider
{
  [NotNull] private static readonly UnderlineTextAnimation ourUnderlineTextAnimation = new();
  
  
  [NotNull] private const string CElementKey = "doc.comment.c.element.text";
  [NotNull] private const string ParamRefKey = "doc.comment.param.ref.text";
  [NotNull] private const string SeeAlsoLinkKey = "see.also.link.text";
  [NotNull] private const string SeeAlsoMemberKey = "see.also.member.text";
  [NotNull] private const string SeeCodeEntityKey = "see.text";
  [NotNull] private const string SeeHttpKey = "see.http";
  [NotNull] private const string SeeLangWord = "see.langword";


  public TextHighlighter GetCXmlElementHighlighter(int startOffset, int endOffset) =>
    new(CElementKey, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes, ourUnderlineTextAnimation);

  public TextHighlighter GetParamRefElementHighlighter(int startOffset, int endOffset) =>
    new(ParamRefKey, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes, ourUnderlineTextAnimation);

  public TextHighlighter GetSeeAlsoLinkHighlighter(int startOffset, int endOffset) =>
    new(SeeAlsoLinkKey, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes, ourUnderlineTextAnimation);

  public TextHighlighter GetSeeAlsoMemberHighlighter(int startOffset, int endOffset) =>
    new(SeeAlsoMemberKey, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes, ourUnderlineTextAnimation);

  public TextHighlighter GetSeeCodeEntityHighlighter(int startOffset, int endOffset) => 
    new(SeeCodeEntityKey, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes, ourUnderlineTextAnimation);

  public TextHighlighter GetSeeHttpLinkHighlighter(int startOffset, int endOffset) =>
    new(SeeHttpKey, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes, ourUnderlineTextAnimation);

  public TextHighlighter GetSeeLangWordHighlighter(int startOffset, int endOffset) =>
    new(SeeLangWord, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes, ourUnderlineTextAnimation);
}