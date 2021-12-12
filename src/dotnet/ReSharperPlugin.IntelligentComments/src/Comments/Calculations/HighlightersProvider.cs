using JetBrains.ProjectModel;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public interface IHighlightersProvider
{
  TextHighlighter GetCXmlElementHighlighter(int startOffset, int endOffset);
  TextHighlighter GetParamRefElementHighlighter(int startOffset, int endOffset);
}
  
  
[SolutionComponent]
public class HighlightersProvider : IHighlightersProvider
{
  private static readonly TextHighlighterAttributes ourDefaultAttributes = new(FontStyle.Regular, true, 400);

  private const string CElementKey = "doc.comment.c.element.text";
  private const string ParamRefKey = "doc.comment.param.ref.text";


  public TextHighlighter GetCXmlElementHighlighter(int startOffset, int endOffset) =>
    new(CElementKey, startOffset, endOffset, ourDefaultAttributes);

  public TextHighlighter GetParamRefElementHighlighter(int startOffset, int endOffset) =>
    new(ParamRefKey, startOffset, endOffset, ourDefaultAttributes);
}