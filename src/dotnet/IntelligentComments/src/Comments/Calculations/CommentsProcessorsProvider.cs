using System;
using IntelligentComments.Comments.Calculations.Core;
using IntelligentComments.Comments.Calculations.Core.Languages.CSharp;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace IntelligentComments.Comments.Calculations;

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