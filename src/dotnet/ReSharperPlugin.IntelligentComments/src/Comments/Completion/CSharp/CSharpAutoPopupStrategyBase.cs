using JetBrains.Application.Settings;
using JetBrains.ReSharper.Feature.Services.CodeCompletion;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Settings;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.TextControl;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;

public abstract class CSharpAutoPopupStrategyBase : IAutomaticCodeCompletionStrategy
{
  public PsiLanguageType Language => CSharpLanguage.Instance!;
  public virtual bool ForceHideCompletion => false;
  

  public virtual AutopopupType IsEnabledInSettings(IContextBoundSettingsStore settingsStore, ITextControl textControl)
  {
    return AutopopupType.HardAutopopup;
  }

  public virtual bool AcceptTyping(char c, ITextControl textControl, IContextBoundSettingsStore settingsStore)
  {
    return true;
  }

  public virtual bool ProcessSubsequentTyping(char c, ITextControl textControl)
  {
    return true;
  }

  public abstract bool AcceptsFile(IFile file, ITextControl textControl);
}