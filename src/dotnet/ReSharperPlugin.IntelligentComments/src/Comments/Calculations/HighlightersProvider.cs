using JetBrains.ProjectModel;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations
{
  public interface IHighlightersProvider
  {
    TextHighlighter GetCXmlElementHighlighter(int startOffset, int endOffset);
  }
  
  
  [SolutionComponent]
  public class HighlightersProvider : IHighlightersProvider
  {
    private const string CElementKey = "doc.comment.c.element.text";


    public TextHighlighter GetCXmlElementHighlighter(int startOffset, int endOffset) =>
      new(CElementKey, startOffset, endOffset, new TextHighlighterAttributes(FontStyle.Regular, true, 400));
  }
}