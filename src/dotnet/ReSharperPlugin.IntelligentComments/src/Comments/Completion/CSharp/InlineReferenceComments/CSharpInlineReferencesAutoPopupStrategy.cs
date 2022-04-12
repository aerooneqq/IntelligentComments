using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.TextControl;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.InlineReferenceComments;

[SolutionComponent]
public class CSharpInlineReferencesAutoPopupStrategy : CSharpAutoPopupStrategyBase
{
  [NotNull] private readonly InlineReferenceCommentCreator myCreator;


  public CSharpInlineReferencesAutoPopupStrategy()
  {
    myCreator = LanguageManager.Instance.GetService<InlineReferenceCommentCreator>(Language);
  }
  
  
  public override bool AcceptsFile(IFile file, ITextControl textControl)
  {
    return this.MatchToken(file, textControl, node =>
    {
      if (NamesResolveUtil.TryFindAnyCommentNode(node) is not { }) return false;

      var caretOffset = textControl.Caret.DocumentOffset();
      return myCreator.TryExtractCompletionInlineReferenceInfo(node, caretOffset) is { };
    });
  }
}