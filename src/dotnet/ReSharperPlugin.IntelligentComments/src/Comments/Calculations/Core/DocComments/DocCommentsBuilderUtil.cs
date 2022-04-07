using System;
using System.Collections.Generic;
using System.Linq;
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

internal record struct TextProcessingResult([NotNull] string ProcessedText, int EffectiveLength);
internal record struct TagInfo([NotNull] IHighlightedText NameText, [NotNull] IHighlightedText DescriptionText);

public enum NameKind
{
  Invariant,
  Todo,
  Hack
}

public record struct NameExtraction([NotNull] string Name, NameKind NameKind);

internal static class DocCommentsBuilderUtil
{
  [NotNull] internal const string ImageTagName = "image";
  [NotNull] internal const string ImageSourceAttrName = "source";
  [NotNull] internal const string ReferenceTagName = "reference";
  [NotNull] internal const string TodoTagName = "todo";
  [NotNull] internal const string DescriptionTagName = "description";
  [NotNull] internal const string TicketsSectionTagName = "tickets";
  [NotNull] internal const string TicketTagName = "ticket";
  [NotNull] internal const string HackTagName = "hack";
  [NotNull] internal const string InvariantTagName = "invariant";

  [NotNull] internal const string CommonNameAttrName = "name";
  [NotNull] internal const string TicketNameAttrName = CommonNameAttrName;
  [NotNull] internal const string HackNameAttrName = CommonNameAttrName;
  [NotNull] internal const string TicketSourceAttrName = "source";
  
  [NotNull] internal const string InvariantReferenceSourceAttrName = "invariant";
  [NotNull] internal const string HackReferenceSourceAttributeName = "hack";
  [NotNull] internal const string TodoReferenceSourceAttributeName = "todo";
  
  [NotNull] internal const string InvariantNameAttrName = CommonNameAttrName;
  [NotNull] internal const string InheritDocTagName = "inheritdoc";
  [NotNull] internal const string CRef = "cref";


  internal static HashSet<string> PossibleNamedEntityTags { get; } = new()
  {
    TodoTagName,
    HackTagName,
    InvariantTagName
  };

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
  internal static HashSet<string> PossibleInnerFirstLevelTagsOfHack { get; } = new()
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
  internal static HashSet<string> PossibleReferenceTagSourceAttributes { get; } = new() 
  {
    InvariantReferenceSourceAttrName,
    HackReferenceSourceAttributeName,
    TodoReferenceSourceAttributeName
  };

  [NotNull] 
  internal static string PossibleReferenceTagAttributesPresentation { get; } = string.Join(", ", PossibleReferenceTagSourceAttributes);


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

  internal static TextProcessingResult PreprocessTextWithContext([NotNull] string text, [CanBeNull] string nextTextSibling)
  {
    char? trailingCharToAdd = null;
    if (nextTextSibling is null)
    {
      if (!(text.Length > 0 && ourCharsWithNoNeedToAddSpaceAfter.Contains(text[^1])))
      {
        trailingCharToAdd = ' ';
      }
    }
    else
    {
      foreach (var c in nextTextSibling)
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
  
  internal static TextProcessingResult PreprocessTextWithContext([NotNull] string text, [NotNull] XmlNode context)
  {
    return PreprocessTextWithContext(text, (context.NextSibling as XmlText)?.Value);
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

  private static NameKind? TryGetNameKindFromTag(string tagName)
  {
    return tagName switch
    {
      InvariantTagName => NameKind.Invariant,
      HackTagName => NameKind.Hack,
      TodoTagName => NameKind.Todo,
      _ => null
    };
  }
  
  internal static NameExtraction? TryExtractNameFrom([NotNull] IXmlTag xmlTag)
  {
    var nameKind = TryGetNameKindFromTag(xmlTag.Header.Name.XmlName);
    if (!nameKind.HasValue) return null;

    if (xmlTag.GetAttribute(CommonNameAttrName) is not { } nameAttr) return null;
    return new NameExtraction(nameAttr.UnquotedValue, nameKind.Value);
  }
  
  internal static bool IsInvariantNameAttribute([CanBeNull] IXmlAttribute attribute)
  {
    return attribute is { AttributeName: InvariantNameAttrName } &&
           CheckIfAttributeBelongsToTag(attribute, InvariantTagName);
  }
  
  internal static bool IsReferenceTag(IXmlTag xmlTag)
  {
    return xmlTag.Header.Name.XmlName == ReferenceTagName;
  }
  
  private static bool CheckIfAttributeBelongsToTag([NotNull] IXmlAttribute attribute, [NotNull] string tagName)
  {
    if (attribute.Parent is not IXmlTagHeader xmlTagHeader) return false;
    return xmlTagHeader.Name.XmlName == tagName;
  }
  
  internal static NameExtraction? TryExtractNameFromPossibleReferenceSourceAttribute([CanBeNull] IXmlAttribute attribute)
  {
    if (attribute is null) return null;
    if (!CheckIfAttributeBelongsToTag(attribute, ReferenceTagName)) return null;

    var attrName = attribute.AttributeName;
    if (!PossibleReferenceTagSourceAttributes.Contains(attrName)) return null;
    
    var nameKind = GetNameKind(attrName);
    return new NameExtraction(attribute.UnquotedValue, nameKind);
  }

  internal static NameKind GetNameKind([NotNull] string attributeName) => attributeName switch
  {
    InvariantReferenceSourceAttrName => NameKind.Invariant,
    TodoReferenceSourceAttributeName => NameKind.Todo,
    HackReferenceSourceAttributeName => NameKind.Hack,
    _ => throw new ArgumentOutOfRangeException(attributeName)
  };
  
  internal static NameKind? TryGetNameKind([NotNull] string attributeName) => attributeName switch
  {
    InvariantReferenceSourceAttrName => NameKind.Invariant,
    TodoReferenceSourceAttributeName => NameKind.Todo,
    HackReferenceSourceAttributeName => NameKind.Hack,
    _ => null
  };

  [CanBeNull]
  internal static string TryGetReferenceAttributeNameFrom(NameKind nameKind) => nameKind switch
  {
    NameKind.Invariant => InvariantReferenceSourceAttrName,
    NameKind.Todo => TodoReferenceSourceAttributeName,
    NameKind.Hack => HackReferenceSourceAttributeName,
    _ => null
  };

  [CanBeNull]
  internal static NameKind? TryExtractOneReferenceNameKindFromReferenceTag([NotNull] XmlElement referenceTag)
  {
    if (referenceTag.Name != ReferenceTagName) return null;
    
    var sourceAttributes = referenceTag.Attributes
      .SafeOfType<XmlAttribute>()
      .Where(attr => PossibleReferenceTagSourceAttributes.Contains(attr.Name))
      .ToList();

    if (sourceAttributes.Count != 1) return null;

    var attribute = sourceAttributes.First();
    return TryGetNameKind(attribute.Name);
  }
  
  [CanBeNull]
  internal static NameExtraction? TryExtractOneReferenceNameKindFromReferenceTag([NotNull] IXmlTag referenceTag)
  {
    if (TryGetOneReferenceSourceAttribute(referenceTag) is not { } attribute) return null; 
    if (TryGetNameKind(attribute.AttributeName) is not { } nameKind) return null;

    return new NameExtraction(attribute.UnquotedValue, nameKind);
  }
  
  [CanBeNull]
  internal static IXmlAttribute TryGetInvariantAttribute([CanBeNull] IXmlTag invariantTag)
  {
    return invariantTag?.GetAttribute(InvariantNameAttrName);
  }
  
  [CanBeNull]
  internal static IXmlAttribute TryGetOneReferenceSourceAttribute([CanBeNull] IXmlTag referenceTag)
  {
    if (referenceTag is null || referenceTag.Header.Name.XmlName != ReferenceTagName) return null;
    
    var sourceAttributes = referenceTag.GetAttributes()
      .Where(attr => PossibleReferenceTagSourceAttributes.Contains(attr.AttributeName))
      .ToList();

    if (sourceAttributes.Count != 1) return null;
    return sourceAttributes.First();
  }

  internal static string GetInvariantName(IXmlAttribute attribute)
  {
    Assertion.Assert(attribute.AttributeName == InvariantNameAttrName, "attribute.AttributeName == InvariantNameAttrName");
    return PreprocessText(attribute.UnquotedValue, null);
  }

  internal static TagInfo? TryExtractTagInfo(
    [NotNull] XmlElement element,
    [NotNull] string attributeName,
    [NotNull] IHighlightersProvider provider,
    [CanBeNull] Func<string, IDomainReference> nameReferenceCreator = null,
    [CanBeNull] Func<IDomainReference, bool> referenceValidityChecker = null)
  {
    var nameText = TryExtractNameAttribute(element, provider, attributeName, nameReferenceCreator, referenceValidityChecker);
    return nameText is null ? null : TryExtractTagInfo(element, provider, nameText);
  }
  
  private static TagInfo? TryExtractDescription(
    [NotNull] string description,
    [CanBeNull] string nextTextSibling,
    [NotNull] IHighlightersProvider provider, 
    [NotNull] IHighlightedText nameText)
  {
    var descriptionText = HighlightedText.CreateEmptyText();
    var (processedText, length) = PreprocessTextWithContext(description, nextTextSibling);
    var descriptionHighlighter = provider.TryGetReSharperHighlighter(DefaultLanguageAttributeIds.DOC_COMMENT, length);
    descriptionText.Add(new HighlightedText(processedText, descriptionHighlighter));
    
    return new TagInfo(nameText, descriptionText);
  }

  private static TagInfo? TryExtractTagInfo(
    [NotNull] XmlElement element, 
    [NotNull] IHighlightersProvider provider, 
    [NotNull] IHighlightedText nameText)
  {
    if (!ElementHasOneTextChild(element, out var description)) return new TagInfo(nameText, HighlightedText.EmptyText);
    return TryExtractDescription(description, (element.NextSibling as XmlText)?.Value, provider, nameText);
  }

  internal static TagInfo? TryExtractTagInfo(
    [NotNull] XmlElement element,
    NameKind nameKind,
    [NotNull] IHighlightersProvider provider,
    [CanBeNull] Func<string, IDomainReference> nameReferenceCreator = null,
    [CanBeNull] Func<IDomainReference, bool> referenceValidityChecker = null)
  {
    var nameText = TryExtractNameAttribute(element, provider, nameKind, nameReferenceCreator, referenceValidityChecker);
    return nameText is null ? null : TryExtractTagInfo(element, provider, nameText);
  }

  internal static IHighlightedText TryExtractNameAttribute(
    [NotNull] XmlElement element,
    [NotNull] IHighlightersProvider highlightersProvider,
    string attributeName,
    [CanBeNull] Func<string, IDomainReference> nameReferenceCreator = null,
    [CanBeNull] Func<IDomainReference, bool> referenceValidityChecker = null)
  {
    var name = element.GetAttribute(attributeName);
    if (name.IsNullOrWhitespace()) return null;

    return CreateNameHighlightedText(name, highlightersProvider, nameReferenceCreator, referenceValidityChecker);
  }

  [NotNull]
  private static IHighlightedText CreateNameHighlightedText(
    [NotNull] string name,
    [NotNull] IHighlightersProvider highlightersProvider,
    [CanBeNull] Func<string, IDomainReference> nameReferenceCreator = null,
    [CanBeNull] Func<IDomainReference, bool> referenceValidityChecker = null)
  {
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
    
    return new HighlightedText(name, nameHighlighter);
  }
  
  [CanBeNull]
  internal static IHighlightedText TryExtractNameAttribute(
    [NotNull] XmlElement element,
    [NotNull] IHighlightersProvider highlightersProvider,
    NameKind nameKind,
    [CanBeNull] Func<string, IDomainReference> nameReferenceCreator = null,
    [CanBeNull] Func<IDomainReference, bool> referenceValidityChecker = null)
  {
    if (TryGetReferenceAttributeNameFrom(nameKind) is not { } attributeName) return null;
    return TryExtractNameAttribute(element, highlightersProvider, attributeName, nameReferenceCreator, referenceValidityChecker);
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