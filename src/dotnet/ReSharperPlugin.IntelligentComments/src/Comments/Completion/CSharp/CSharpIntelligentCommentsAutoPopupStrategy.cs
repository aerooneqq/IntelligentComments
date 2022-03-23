using JetBrains.Application.Settings;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Settings;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.TextControl;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;

[SolutionComponent]
public class CSharpIntelligentCommentsAutoPopupStrategy : IAutomaticCodeCompletionStrategy
{
  public PsiLanguageType Language => CSharpLanguage.Instance;
  public bool ForceHideCompletion => false;
  
  
  public AutopopupType IsEnabledInSettings(IContextBoundSettingsStore settingsStore, ITextControl textControl)
  {
    return AutopopupType.HardAutopopup;
  }

  public bool AcceptTyping(char c, ITextControl textControl, IContextBoundSettingsStore settingsStore)
  {
    return true;
  }

  public bool ProcessSubsequentTyping(char c, ITextControl textControl)
  {
    return true;
  }

  public bool AcceptsFile(IFile file, ITextControl textControl)
  {
    return this.MatchToken(file, textControl, node =>
    {
      var docComment = node.TryFindDocCommentBlock();
      var offset = textControl.Caret.DocumentOffset();
      var token = CSharpIntelligentCommentCompletionContextProvider.TryGetXmlToken(docComment, offset);
      if (token is not IXmlValueToken { Parent: IXmlAttribute parent }) return false;

      return CommentsBuilderUtil.PossibleReferenceTagAttributes.Contains(parent.AttributeName) ||
             parent.AttributeName == CommentsBuilderUtil.InvariantNameAttrName;
    });
  }
}