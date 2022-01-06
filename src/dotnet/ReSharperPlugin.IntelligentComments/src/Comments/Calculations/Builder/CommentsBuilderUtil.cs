using System.Collections.Generic;
using System.Text;
using System.Xml;
using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Builder;

internal static class CommentsBuilderUtil
{
  [NotNull] private static readonly ISet<char> ourCharsWithNoNeedToAddSpaceAfter = new HashSet<char>
  {
    '(', '[', '{',
  };
  
  [NotNull] private static readonly ISet<char> ourCharsWithNoNeedToAddSpaceBefore = new HashSet<char>
  {
    ')', ']', '}'
  };
  
  [NotNull] private static readonly ISet<char> ourWhitespaceChars = new HashSet<char> { ' ', '\n', '\r', '\t' };


  private static string PreprocessText([NotNull] string text, char? trailingCharToAdd)
  {
    var sb = new StringBuilder(text);
    while (ourWhitespaceChars.Contains(sb[0]))
    {
      sb.Remove(0, 1);
    }
      
    while (ourWhitespaceChars.Contains(sb[^1]))
    {
      sb.Remove(sb.Length - 1, 1);
    }
    
    for (int i = sb.Length - 1; i >= 0; --i)
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

    return text.Replace("\n ", "\n").Replace(" \n", "\n");
  }

  internal static (string, bool) PreprocessTextWithContext([NotNull] string text, [NotNull] XmlNode context)
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
    
    return (PreprocessText(text, trailingCharToAdd), trailingCharToAdd is { });
  }
}