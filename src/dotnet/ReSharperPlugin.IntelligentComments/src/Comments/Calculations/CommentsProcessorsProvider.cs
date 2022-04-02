using System;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.CSharp;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public static class CommentsProcessorsProvider
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