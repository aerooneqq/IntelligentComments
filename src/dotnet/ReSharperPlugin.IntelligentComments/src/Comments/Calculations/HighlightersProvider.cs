using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Daemon.CSharp.Highlighting;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.TextControl.DocumentMarkup;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public interface IHighlightersProvider
{
  [CanBeNull] TextHighlighter TryGetReSharperHighlighter([NotNull] string resharperAttributeId, int length);
  [CanBeNull] TextHighlighter TryGetReSharperHighlighter(int textLength, [NotNull] IDeclaredElement element);
  
  [NotNull] TextHighlighter GetCXmlElementHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetParamRefElementHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeAlsoLinkHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeAlsoMemberHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeCodeEntityHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeHttpLinkHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeLangWordHighlighter(int startOffset, int endOffset);

  [NotNull] TextHighlighter GetExceptionHighlighter(int startOffset, int endOffset);
  
  
  [NotNull]
  TextHighlighter GetReSharperSeeCodeEntityHighlighter(int startOffset, int endOffset, [CanBeNull] IDeclaredElement element);
  
  [NotNull]
  TextHighlighter GetSeeAlsoReSharperMemberHighlighter(int startOffset, int endOffset, [CanBeNull] IDeclaredElement element);
  
  [NotNull]
  TextHighlighter GetReSharperExceptionHighlighter(int startOffset, int endOffset, [CanBeNull] IDeclaredElement element);
}
  
  
[Language(typeof(KnownLanguage))]
[SolutionComponent]
public class HighlightersProvider : IHighlightersProvider
{
  [NotNull] private const string CElementKey = "doc.comment.c.element.text";
  [NotNull] private const string ParamRefKey = "doc.comment.param.ref.text";
  [NotNull] private const string SeeAlsoLinkKey = "see.also.link.text";
  [NotNull] private const string SeeAlsoMemberKey = "see.also.member.text";
  [NotNull] private const string SeeCodeEntityKey = "see.text";
  [NotNull] private const string SeeHttpKey = "see.http";
  [NotNull] private const string SeeLangWord = "see.langword";
  [NotNull] private const string Exception = "doc.comment.exception.name";

  
  [NotNull] private readonly IHighlightingAttributeIdProvider myAttributeIdProvider;
  [NotNull] private readonly IRiderHighlighterNamesProvider myHighlighterNamesProvider;
  

  public HighlightersProvider()
  {
    myAttributeIdProvider = LanguageManager.Instance.GetService<IHighlightingAttributeIdProvider>(CSharpLanguage.Instance!);
    myHighlighterNamesProvider = new CSharpHighlighterGroup.CSharpSettingsNamesProvider();
  }

  
  public TextHighlighter GetCXmlElementHighlighter(int startOffset, int endOffset) => Get(CElementKey, startOffset, endOffset);
  public TextHighlighter GetParamRefElementHighlighter(int startOffset, int endOffset) => Get(ParamRefKey, startOffset, endOffset);
  public TextHighlighter GetSeeAlsoLinkHighlighter(int startOffset, int endOffset) => Get(SeeAlsoLinkKey, startOffset, endOffset);
  public TextHighlighter GetSeeAlsoMemberHighlighter(int startOffset, int endOffset) => Get(SeeAlsoMemberKey, startOffset, endOffset);
  public TextHighlighter GetSeeCodeEntityHighlighter(int startOffset, int endOffset) => Get(SeeCodeEntityKey, startOffset, endOffset);
  public TextHighlighter GetSeeHttpLinkHighlighter(int startOffset, int endOffset) => Get(SeeHttpKey, startOffset, endOffset);
  public TextHighlighter GetSeeLangWordHighlighter(int startOffset, int endOffset) => Get(SeeLangWord, startOffset, endOffset);
  public TextHighlighter GetExceptionHighlighter(int startOffset, int endOffset) => Get(Exception, startOffset, endOffset);
  
  private static TextHighlighter Get(string key, int startOffset, int endOffset) =>
    new(key, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes, UnderlineTextAnimation.Instance);
  
  public TextHighlighter GetReSharperSeeCodeEntityHighlighter(int startOffset, int endOffset, IDeclaredElement element)
  {
    return TryGetHighlighterWithReSharperId(startOffset, endOffset, element) ??
           GetSeeCodeEntityHighlighter(startOffset, endOffset);
  }
  
  [CanBeNull]
  private TextHighlighter TryGetHighlighterWithReSharperId(
    int startOffset, int endOffset, [CanBeNull] IDeclaredElement element)
  {
    if (TryGetAttributeId(element) is { } attributeId)
    {
      return new TextHighlighter(
        attributeId, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes,
        TextAnimation: UnderlineTextAnimation.Instance,
        IsResharperHighlighter: true);
    }

    return null;
  }

  public TextHighlighter TryGetReSharperHighlighter(string resharperAttributeId, int length)
  {
    var id = myHighlighterNamesProvider.GetExternalName(resharperAttributeId);
    return new TextHighlighter(id, 0, length, TextHighlighterAttributes.DefaultAttributes, IsResharperHighlighter: true);
  }

  public TextHighlighter TryGetReSharperHighlighter(int textLength, IDeclaredElement element)
  {
    return TryGetHighlighterWithReSharperId(0, textLength, element);
  } 
  
  [CanBeNull]
  private string TryGetAttributeId([CanBeNull] IDeclaredElement element)
  {
    if (element is { } &&
        myAttributeIdProvider.GetHighlightingAttributeId(element, false) is { } attributeId)
    {
      return myHighlighterNamesProvider.GetExternalName(attributeId);
    }

    return null;
  }
  
  public TextHighlighter GetSeeAlsoReSharperMemberHighlighter(int startOffset, int endOffset, IDeclaredElement element)
  {
    return TryGetHighlighterWithReSharperId(startOffset, endOffset, element) ??
           GetSeeAlsoMemberHighlighter(startOffset, endOffset);
  }

  public TextHighlighter GetReSharperExceptionHighlighter(int startOffset, int endOffset, IDeclaredElement element)
  {
    return TryGetHighlighterWithReSharperId(startOffset, endOffset, element) ??
           GetExceptionHighlighter(startOffset, endOffset);
  }
}