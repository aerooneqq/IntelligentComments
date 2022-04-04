using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using JetBrains.Annotations;
using JetBrains.Diagnostics;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.ExtensionsAPI;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Util;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

internal record struct TextProcessingResult(string ProcessedText, int EffectiveLength);
internal record struct TagInfo([NotNull] IHighlightedText NameText, [NotNull] IHighlightedText DescriptionText);

internal static class DocCommentsBuilderUtil
{
  [NotNull] internal const string ImageTagName = "image";
  [NotNull] internal const string ImageSourceAttrName = "source";
  [NotNull] internal const string ReferenceTagName = "reference";
  [NotNull] internal const string TodoTagName = "todo";
  [NotNull] internal const string DescriptionTagName = "description";
  [NotNull] internal const string TicketsSectionTagName = "tickets";
  [NotNull] internal const string TicketTagName = "ticket";

  [NotNull] internal const string TicketSourceAttrName = "source";
  [NotNull] internal const string InvariantReferenceSourceAttrName = "invariant";
  [NotNull] internal const string InvariantTagName = "invariant";
  [NotNull] internal const string InvariantNameAttrName = "name";
  [NotNull] internal const string InheritDocTagName = "inheritdoc";
  [NotNull] internal const string CRef = "cref";

  internal static HashSet<string> PossibleTicketAttributes { get; } = new()
  {
    TicketSourceAttrName
  };

  [NotNull]
  internal static HashSet<string> PossibleInnerFirstLevelTagsOfTodo { get; } = new()
  {
    DescriptionTagName,
    TicketsSectionTagName
  };

  [NotNull]
  internal static HashSet<string> PossibleInnerFirstLevelTagsOfTicketsSection { get; } = new()
  {
    TicketTagName
  };

  [NotNull] 
  internal static HashSet<string> PossibleReferenceTagAttributes { get; } = new() 
  {
    InvariantReferenceSourceAttrName
  };

  [NotNull] 
  internal static string PossibleReferenceTagAttributesPresentation { get; } = string.Join(", ", PossibleReferenceTagAttributes);


  [NotNull] private static readonly ISet<char> ourCharsWithNoNeedToAddSpaceAfter = new HashSet<char>
  {
    '(', '[', '{',
  };
  
  [NotNull] private static readonly ISet<char> ourWhitespaceChars = new HashSet<char> { ' ', '\n', '\r', '\t' };


  [NotNull]
  public static string PreprocessText([NotNull] string text, char? trailingCharToAdd)
  {
    if (text.Length == 0) return text;
    
    var sb = new StringBuilder(text);
    while (sb.Length > 0 && ourWhitespaceChars.Contains(sb[0]))
    {
      sb.Remove(0, 1);
    }
    
    while (sb.Length > 0 && ourWhitespaceChars.Contains(sb[^1]))
    {
      sb.Remove(sb.Length - 1, 1);
    }
    
    for (var i = sb.Length - 1; i >= 0; --i)
    {
      if (sb[i] == '\r') sb.Remove(i, 1);
    }

    if (trailingCharToAdd is { })
    {
      sb.Append(trailingCharToAdd.Value);
    }
    
    text = sb.ToString();
    
    while (text.Contains("  "))
    {
      text = text.Replace("  ", " ");
    }

    return text.Replace("\n\n", "\n").Replace("\n ", "\n").Replace(" \n", "\n");
  }
  
  internal static TextProcessingResult PreprocessTextWithContext([NotNull] string text, [NotNull] XmlNode context)
  {
    var nextSibling = context.NextSibling;

    char? trailingCharToAdd = null;
    if (nextSibling is not XmlText xmlText)
    {
      if (!(text.Length > 0 && ourCharsWithNoNeedToAddSpaceAfter.Contains(text[^1])))
      {
        trailingCharToAdd = ' ';
      }
    }
    else
    {
      foreach (var c in xmlText.Value)
      {
        if (c == '\n')
        {
          trailingCharToAdd = '\n';
          break;
        }

        if (c == ' ')
        {
          trailingCharToAdd = ' ';
        }
        else
        {
          break;
        }
      }
    }

    text = PreprocessText(text, trailingCharToAdd);
    
    return new TextProcessingResult(text, trailingCharToAdd is { } ? text.Length - 1 : text.Length);
  }

  [CanBeNull] 
  internal static XmlNode TryGetXml([NotNull] IDocCommentBlock comment) => comment.GetXML(null); 
  
  internal static bool IsInheritDocComment([NotNull] IDocCommentBlock comment)
  {
    if (TryGetXml(comment) is not { } xmlNode) return false;

    return IsInheritDocComment(xmlNode);
  }

  internal static bool IsInheritDocComment([NotNull] XmlNode commentTopMostNode)
  {
    return commentTopMostNode.FirstChild is XmlElement { Name: InheritDocTagName };
  }

  [CanBeNull]
  internal static IDocCommentBlock TryGetAdjustedComment([NotNull] IDocCommentBlock commentBlock)
  {
    if (!IsInheritDocComment(commentBlock)) return commentBlock;

    if (commentBlock.GetXML(null) is not { FirstChild: XmlElement inheritDocElement }) return null;

    IDeclaredElement element;
    if (inheritDocElement.GetAttributeNode(CRef) is { } crefAttribute)
    {
      var services = commentBlock.GetPsiServices();
      var module = commentBlock.GetPsiModule();
      element = XMLDocUtil.ResolveId(services, crefAttribute.Value, module, true);

      return element is { } ? TryGetDocCommentBlockFor(element) : null;
    }

    var commentOwner = commentBlock.Parent;
    if (commentOwner is not IDeclaration { DeclaredElement: { } declaredElement }) return null;
    element = declaredElement;

    if (element is not IOverridableMember overridableMember) return commentBlock;

    foreach (var superMember in overridableMember.GetImmediateSuperMembers())
    {
      var member = superMember.Member;
      if (TryGetDocCommentBlockFor(member) is { } docCommentBlock)
      {
        return docCommentBlock;
      }
    }

    return commentBlock;
  }

  [CanBeNull]
  internal static IDocCommentBlock TryGetDocCommentBlockFor([NotNull] IDeclaredElement declaredElement)
  {
    foreach (var declaration in declaredElement.GetDeclarations())
    {
      if (SharedImplUtil.GetDocCommentBlockNode(declaration) is { } docCommentBlock &&
          !IsInheritDocComment(docCommentBlock))
      {
        return docCommentBlock;
      }
    }

    return null;
  }

  [CanBeNull]
  internal static string TryGetInvariantName([NotNull] XmlElement element)
  {
    if (element.LocalName != InvariantTagName) return null;
    
    return element.GetAttribute(InvariantNameAttrName);
  }
  
  internal static bool IsInvariantNameAttribute([CanBeNull] IXmlAttribute attribute)
  {
    return attribute is { AttributeName: InvariantNameAttrName } &&
           CheckIfAttributeBelongsToTag(attribute, InvariantTagName);
  }
  
  internal static bool IsReferenceTagWithInvariantSource(IXmlTag xmlTag)
  {
    return xmlTag.Header.Name.XmlName == ReferenceTagName &&
           xmlTag.GetAttribute(InvariantReferenceSourceAttrName) is { };
  }
  
  private static bool CheckIfAttributeBelongsToTag([NotNull] IXmlAttribute attribute, [NotNull] string tagName)
  {
    if (attribute.Parent is not IXmlTagHeader xmlTagHeader) return false;
    return xmlTagHeader.Name.XmlName == tagName;
  }
  
  
  internal static bool IsInvariantReferenceSourceAttribute([CanBeNull] IXmlAttribute attribute)
  {
    return attribute is { AttributeName: InvariantReferenceSourceAttrName } &&
           CheckIfAttributeBelongsToTag(attribute, ReferenceTagName);
  }

  [CanBeNull]
  internal static IXmlAttribute TryGetInvariantAttribute([CanBeNull] IXmlTag invariantTag)
  {
    return invariantTag?.GetAttribute(InvariantNameAttrName);
  }
  
  [CanBeNull]
  internal static IXmlAttribute TryGetInvariantReferenceSourceAttribute([CanBeNull] IXmlTag referenceTag)
  {
    return referenceTag?.GetAttribute(InvariantReferenceSourceAttrName);
  }

  internal static string GetInvariantName(IXmlAttribute attribute)
  {
    Assertion.Assert(attribute.AttributeName == InvariantNameAttrName, "attribute.AttributeName == InvariantNameAttrName");
    return PreprocessText(attribute.UnquotedValue, null);
  }
  
  internal static TagInfo? TryExtractTagInfo(
    [NotNull] XmlElement element, 
    [NotNull] string attributeName,
    [NotNull] IHighlightersProvider highlightersProvider,
    [CanBeNull] Func<string, IDomainReference> nameReferenceCreator = null,
    [CanBeNull] Func<IDomainReference, bool> referenceValidityChecker = null)
  {
    var name = element.GetAttribute(attributeName);
    if (name.IsNullOrWhitespace()) return null;

    var nameHighlighter = highlightersProvider.TryGetReSharperHighlighter(DefaultLanguageAttributeIds.DOC_COMMENT, name.Length);
    if (nameHighlighter is { } && nameReferenceCreator is { })
    {
      var reference = nameReferenceCreator(name);
      var isValid = referenceValidityChecker?.Invoke(reference) ?? true;

      if (!isValid)
      {
        nameHighlighter = highlightersProvider.TryGetDocCommentHighlighterWithErrorSquiggles(name.Length);
      }

      if (nameHighlighter is { })
      {
        nameHighlighter = nameHighlighter with
        {
          Attributes = nameHighlighter.Attributes with { FontStyle = FontStyle.Bold },
          References = new[] { reference },
          TextAnimation = UnderlineTextAnimation.Instance
        };
      }
    }
    
    var nameText = new HighlightedText(name, nameHighlighter);
    var descriptionText = HighlightedText.CreateEmptyText();
    if (ElementHasOneTextChild(element, out var description))
    {
      var (processedText, length) = PreprocessTextWithContext(description, element);
      var descriptionHighlighter = highlightersProvider.TryGetReSharperHighlighter(DefaultLanguageAttributeIds.DOC_COMMENT, length);
      descriptionText.Add(new HighlightedText(processedText, descriptionHighlighter));
    }

    return new TagInfo(nameText, descriptionText);
  }
  
  internal static bool ElementHasOneTextChild([NotNull] XmlElement element, [NotNull] out string value)
  {
    var hasOneTextChild = element.ChildNodes.Count == 1 && element.FirstChild is XmlText { Value: { } };

    value = hasOneTextChild switch
    {
      true => element.FirstChild.Value,
      false => string.Empty
    };

    return hasOneTextChild;
  }
}