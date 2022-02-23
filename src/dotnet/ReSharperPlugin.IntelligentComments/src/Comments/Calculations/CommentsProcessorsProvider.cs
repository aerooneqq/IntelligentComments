using System;
using JetBrains.Application;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors.CSharp;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

[ShellComponent]
public class CommentsProcessorsProvider
{
  public static ICommentsProcessor CreateProcessorFor(PsiLanguageType languageType)
  {
    return languageType.Name switch
    {
      CSharpLanguage.Name => new CSharpCommentsProcessor(),
      _ => throw new ArgumentOutOfRangeException(languageType.Name)
    };
  }
}