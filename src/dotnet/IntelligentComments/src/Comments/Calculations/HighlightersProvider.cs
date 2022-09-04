using System.Drawing;
using IntelligentComments.Comments.Domain.Core;
using IntelligentComments.Comments.Domain.Core.References;
using JetBrains.Annotations;
using JetBrains.ReSharper.Daemon.CSharp.Highlighting;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.TextControl.DocumentMarkup;

namespace IntelligentComments.Comments.Calculations;

public interface IHighlightersProvider
{
  [CanBeNull]
  TextHighlighter TryGetReSharperHighlighter([NotNull] string resharperAttributeId, int length);
  
  [CanBeNull] 
  TextHighlighter TryGetReSharperHighlighter(int textLength, [NotNull] IDomainReference domainReference, [NotNull] IDomainResolveContext context);

  [CanBeNull] 
  TextHighlighter TryGetDocCommentHighlighterWithErrorSquiggles(int length);

  [CanBeNull]
  TextHighlighter TryGetDocCommentHighlighter(int length);
  
  
  [NotNull] TextHighlighter GetCXmlElementHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetParamRefElementHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeAlsoLinkHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeAlsoMemberHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeCodeEntityHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeHttpLinkHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeLangWordHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetExceptionHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetErrorHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetToDoHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetHackHighlighter(int startOffset, int endOffset);
  
  
  [NotNull]
  TextHighlighter GetReSharperSeeCodeEntityHighlighter(
    int startOffset, int endOffset, [CanBeNull] IDomainReference domainReference, [NotNull] IDomainResolveContext context);
  
  [NotNull]
  TextHighlighter GetSeeAlsoReSharperMemberHighlighter(
    int startOffset, int endOffset, [CanBeNull] IDomainReference domainReference, [NotNull] IDomainResolveContext context);
  
  [NotNull]
  TextHighlighter GetReSharperExceptionHighlighter(
    int startOffset, int endOffset, [CanBeNull] IDomainReference domainReference, [NotNull] IDomainResolveContext context);
}


public abstract class HighlightersProvider : IHighlightersProvider
{
  [NotNull] private const string ErrorElementKey = "comment.error";
  [NotNull] private const string CElementKey = "doc.comment.c.element.text";
  [NotNull] private const string ParamRefKey = "doc.comment.param.ref.text";
  [NotNull] private const string SeeAlsoLinkKey = "see.also.link.text";
  [NotNull] private const string SeeAlsoMemberKey = "see.also.member.text";
  [NotNull] private const string SeeCodeEntityKey = "see.text";
  [NotNull] private const string SeeHttpKey = "see.http";
  [NotNull] private const string SeeLangWord = "see.langword";
  [NotNull] private const string Exception = "doc.comment.exception.name";
  [NotNull] private const string ToDoText = "todo.text.color";
  [NotNull] private const string HackText = "hack.text.color";

  
  [NotNull] private readonly IHighlightingAttributeIdProvider myAttributeIdProvider;
  [NotNull] private readonly IRiderHighlighterNamesProvider myHighlighterNamesProvider;
  

  protected HighlightersProvider(
    [NotNull] IHighlightingAttributeIdProvider attributeIdProvider,
    [NotNull] IRiderHighlighterNamesProvider highlighterNamesProvider)
  {
    myAttributeIdProvider = attributeIdProvider;
    myHighlighterNamesProvider = highlighterNamesProvider;
  }

  
  public TextHighlighter GetCXmlElementHighlighter(int startOffset, int endOffset) => 
    Get(CElementKey, startOffset, endOffset);
  
  public TextHighlighter GetParamRefElementHighlighter(int startOffset, int endOffset) => 
    Get(ParamRefKey, startOffset, endOffset);
  
  public TextHighlighter GetSeeAlsoLinkHighlighter(int startOffset, int endOffset) => 
    Get(SeeAlsoLinkKey, startOffset, endOffset);
  
  public TextHighlighter GetSeeAlsoMemberHighlighter(int startOffset, int endOffset) => 
    Get(SeeAlsoMemberKey, startOffset, endOffset);
  
  public TextHighlighter GetSeeCodeEntityHighlighter(int startOffset, int endOffset) => 
    Get(SeeCodeEntityKey, startOffset, endOffset);
  
  public TextHighlighter GetSeeHttpLinkHighlighter(int startOffset, int endOffset)
  {
    if (TryGetDocCommentHighlighter(endOffset) is not { } highlighter)
    {
      return Get(SeeHttpKey, startOffset, endOffset);
    }

    return highlighter with
    {
      Attributes = highlighter.Attributes with { Underline = true },
      TextAnimation = new ForegroundTextAnimation(Color.MediumTurquoise)
    };
  }

  public TextHighlighter GetSeeLangWordHighlighter(int startOffset, int endOffset)
  {
    if (TryGetReSharperHighlighter(DefaultLanguageAttributeIds.KEYWORD, endOffset) is not { } highlighter)
    {
      return Get(SeeLangWord, startOffset, endOffset);
    }

    return highlighter;
  }
  
  public TextHighlighter GetExceptionHighlighter(int startOffset, int endOffset) => 
    Get(Exception, startOffset, endOffset);

  public TextHighlighter GetToDoHighlighter(int startOffset, int endOffset)
  {
    return Get("ReSharper.TODO_ITEM_NORMAL", startOffset, endOffset) with { IsResharperHighlighter = true };
  }

  public TextHighlighter GetHackHighlighter(int startOffset, int endOffset) =>
    Get(HackText, startOffset, endOffset);

  public TextHighlighter GetErrorHighlighter(int startOffset, int endOffset) => Get(ErrorElementKey, startOffset, endOffset);

  public TextHighlighter TryGetDocCommentHighlighterWithErrorSquiggles(int length)
  {
    if (TryGetReSharperHighlighter(DefaultLanguageAttributeIds.DOC_COMMENT, length) is not { } highlighter)
    {
      return null;
    }

    return highlighter with
    {
      ErrorSquiggles = new Squiggles(SquigglesKind.Wave, ErrorElementKey)
    };
  }
  
  public TextHighlighter TryGetDocCommentHighlighter(int length)
  {
    return TryGetReSharperHighlighter(DefaultLanguageAttributeIds.DOC_COMMENT, length);
  }
  
  private static TextHighlighter Get(string key, int startOffset, int endOffset) =>
    new(key, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes, TextAnimation: UnderlineTextAnimation.Instance);
  
  public TextHighlighter GetReSharperSeeCodeEntityHighlighter(
    int startOffset, int endOffset, IDomainReference domainReference, IDomainResolveContext context)
  {
    return TryGetHighlighterWithReSharperId(startOffset, endOffset, domainReference, context) ??
           GetSeeCodeEntityHighlighter(startOffset, endOffset);
  }
  
  [CanBeNull]
  private TextHighlighter TryGetHighlighterWithReSharperId(
    int startOffset, int endOffset, [CanBeNull] IDomainReference domainReference, [NotNull] IDomainResolveContext context)
  {
    if (domainReference is { } && TryGetAttributeId(domainReference, context) is { } attributeId)
    {
      return new TextHighlighter(
        attributeId, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes,
        References: new [] { domainReference },
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

  public TextHighlighter TryGetReSharperHighlighter(
    int textLength, IDomainReference domainReference, IDomainResolveContext context)
  {
    return TryGetHighlighterWithReSharperId(0, textLength, domainReference, context);
  } 
  
  [CanBeNull]
  private string TryGetAttributeId([CanBeNull] IDomainReference domainReference, IDomainResolveContext context)
  {
    if (domainReference?.Resolve(context) is DeclaredElementDomainResolveResult { DeclaredElement: { } declaredElement } && 
        myAttributeIdProvider.GetHighlightingAttributeId(declaredElement, false) is { } attributeId)
    {
      return myHighlighterNamesProvider.GetExternalName(attributeId);
    }

    return null;
  }
  
  public TextHighlighter GetSeeAlsoReSharperMemberHighlighter(
    int startOffset, int endOffset, IDomainReference domainReference, IDomainResolveContext context)
  {
    return TryGetHighlighterWithReSharperId(startOffset, endOffset, domainReference, context) ??
           GetSeeAlsoMemberHighlighter(startOffset, endOffset);
  }

  public TextHighlighter GetReSharperExceptionHighlighter(
    int startOffset, int endOffset, IDomainReference domainReference, IDomainResolveContext context)
  {
    return TryGetHighlighterWithReSharperId(startOffset, endOffset, domainReference, context) ??
           GetExceptionHighlighter(startOffset, endOffset);
  }
}

[Language(typeof(CSharpLanguage))]
public class CSharpHighlightersProvider : HighlightersProvider
{
  public CSharpHighlightersProvider(IHighlightingAttributeIdProvider provider) 
    : base(provider, new CSharpHighlighterGroup.CSharpSettingsNamesProvider())
  {
  }
}