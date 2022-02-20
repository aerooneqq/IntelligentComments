using System.Collections.Generic;
using System.Text;
using System.Xml;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Util;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;

internal record struct TextProcessingResult(string ProcessedText, int EffectiveLength);

internal static class CommentsBuilderUtil
{
  [NotNull] internal const string ImageTagName = "image";
  [NotNull] internal const string ImageSourceAttrName = "source";
  [NotNull] internal const string ReferenceTagName = "reference";
  [NotNull] internal const string ReferenceSourceAttrName = "referenceSource";
  [NotNull] internal const string InvariantTagName = "invariant";
  [NotNull] internal const string InvariantNameAttrName = "name";
  [NotNull] internal const string InheritDocTagName = "inheritdoc";
  [NotNull] internal const string CRef = "cref";


  
  [NotNull] private static readonly ISet<char> ourCharsWithNoNeedToAddSpaceAfter = new HashSet<char>
  {
    '(', '[', '{',
  };
  
  [NotNull] private static readonly ISet<char> ourWhitespaceChars = new HashSet<char> { ' ', '\n', '\r', '\t' };


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

  internal static XmlNode TryGetXml([NotNull] IDocCommentBlock comment) => comment.GetXML(null); 
  
  internal static bool IsInheritDocComment([NotNull] IDocCommentBlock comment)
  {
    var xmlNode = TryGetXml(comment);
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

    if (commentBlock.GetXML(null) is not XmlElement inheritDocElement) return null;

    IDeclaredElement element;
    if (inheritDocElement.GetAttributeNode(CRef) is { } crefAttribute)
    {
      var services = commentBlock.GetPsiServices();
      var module = commentBlock.GetPsiModule();
      element = XMLDocUtil.ResolveId(services, crefAttribute.Value, module, true);
    }
    else
    {
      var commentOwner = commentBlock.Parent;
      if (commentOwner is not IDeclaration { DeclaredElement: { } declaredElement }) return null;
      element = declaredElement;
    }

    if (element is null) return null;

    foreach (var declaration in element.GetDeclarations())
    {
      if (declaration.FirstChild is IDocCommentBlock docCommentBlock)
      {
        return docCommentBlock;
      }
    }

    return null;
  }
}