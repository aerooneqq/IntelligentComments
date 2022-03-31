using System;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors.CSharp;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public class CommentsProcessorsProvider
{
  public static ICommentsProcessor CreateProcessorFor(PsiLanguageType languageType, DaemonProcessKind daemonProcessKind)
  {
    return languageType.Name switch
    {
      CSharpLanguage.Name => new CSharpCommentsProcessor(daemonProcessKind),
      _ => throw new ArgumentOutOfRangeException(languageType.Name)
    };
  }
}