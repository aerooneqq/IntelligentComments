using JetBrains.Annotations;
using JetBrains.ReSharper.Daemon.CSharp.Highlighting;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.TextControl.DocumentMarkup;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public interface IHighlightersProvider
{
  [CanBeNull] 
  TextHighlighter TryGetReSharperHighlighter([NotNull] string resharperAttributeId, int length);
  
  [CanBeNull] 
  TextHighlighter TryGetReSharperHighlighter(int textLength, [NotNull] IReference reference, [NotNull] IResolveContext context);

  [CanBeNull] 
  TextHighlighter TryGetDocCommentHighlighterWithErrorSquiggles(int length);
  
  
  [NotNull] TextHighlighter GetCXmlElementHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetParamRefElementHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeAlsoLinkHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeAlsoMemberHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeCodeEntityHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeHttpLinkHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetSeeLangWordHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetExceptionHighlighter(int startOffset, int endOffset);
  [NotNull] TextHighlighter GetErrorHighlighter(int startOffset, int endOffset);
  
  
  [NotNull]
  TextHighlighter GetReSharperSeeCodeEntityHighlighter(
    int startOffset, int endOffset, [CanBeNull] IReference reference, [NotNull] IResolveContext context);
  
  [NotNull]
  TextHighlighter GetSeeAlsoReSharperMemberHighlighter(
    int startOffset, int endOffset, [CanBeNull] IReference reference, [NotNull] IResolveContext context);
  
  [NotNull]
  TextHighlighter GetReSharperExceptionHighlighter(
    int startOffset, int endOffset, [CanBeNull] IReference reference, [NotNull] IResolveContext context);
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
  public TextHighlighter GetSeeHttpLinkHighlighter(int startOffset, int endOffset) => 
    Get(SeeHttpKey, startOffset, endOffset);
  public TextHighlighter GetSeeLangWordHighlighter(int startOffset, int endOffset) => 
    Get(SeeLangWord, startOffset, endOffset);
  public TextHighlighter GetExceptionHighlighter(int startOffset, int endOffset) => 
    Get(Exception, startOffset, endOffset);

  public TextHighlighter GetErrorHighlighter(int startOffset, int endOffset) => Get(ErrorElementKey, startOffset, endOffset);

  public TextHighlighter TryGetDocCommentHighlighterWithErrorSquiggles(int length)
  {
    if (TryGetReSharperHighlighter(DefaultLanguageAttributeIds.DOC_COMMENT, length) is not { } highlighter)
    {
      return null;
    }

    return highlighter with
    {
      Squiggles = new Squiggles(SquigglesKind.Wave, ErrorElementKey)
    };
  }
  
  
  private static TextHighlighter Get(string key, int startOffset, int endOffset) =>
    new(key, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes, TextAnimation: UnderlineTextAnimation.Instance);
  
  public TextHighlighter GetReSharperSeeCodeEntityHighlighter(
    int startOffset, int endOffset, IReference reference, IResolveContext context)
  {
    return TryGetHighlighterWithReSharperId(startOffset, endOffset, reference, context) ??
           GetSeeCodeEntityHighlighter(startOffset, endOffset);
  }
  
  [CanBeNull]
  private TextHighlighter TryGetHighlighterWithReSharperId(
    int startOffset, int endOffset, [CanBeNull] IReference reference, IResolveContext context)
  {
    if (reference is { } && TryGetAttributeId(reference, context) is { } attributeId)
    {
      return new TextHighlighter(
        attributeId, startOffset, endOffset, TextHighlighterAttributes.DefaultAttributes,
        References: new [] { reference },
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
    int textLength, IReference reference, IResolveContext context)
  {
    return TryGetHighlighterWithReSharperId(0, textLength, reference, context);
  } 
  
  [CanBeNull]
  private string TryGetAttributeId([CanBeNull] IReference reference, IResolveContext context)
  {
    if (reference?.Resolve(context) is DeclaredElementResolveResult { DeclaredElement: { } declaredElement } && 
        myAttributeIdProvider.GetHighlightingAttributeId(declaredElement, false) is { } attributeId)
    {
      return myHighlighterNamesProvider.GetExternalName(attributeId);
    }

    return null;
  }
  
  public TextHighlighter GetSeeAlsoReSharperMemberHighlighter(
    int startOffset, int endOffset, IReference reference, IResolveContext context)
  {
    return TryGetHighlighterWithReSharperId(startOffset, endOffset, reference, context) ??
           GetSeeAlsoMemberHighlighter(startOffset, endOffset);
  }

  public TextHighlighter GetReSharperExceptionHighlighter(
    int startOffset, int endOffset, IReference reference, IResolveContext context)
  {
    return TryGetHighlighterWithReSharperId(startOffset, endOffset, reference, context) ??
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