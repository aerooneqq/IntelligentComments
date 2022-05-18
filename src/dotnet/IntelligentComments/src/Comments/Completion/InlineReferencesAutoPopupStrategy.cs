using IntelligentComments.Comments.Caches.Names;
using IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;
using IntelligentComments.Comments.Languages.CSharp.Completion;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.TextControl;

namespace IntelligentComments.Comments.Completion;

[SolutionComponent]
public class InlineReferencesAutoPopupStrategy : CSharpAutoPopupStrategyBase
{
  [NotNull] private readonly InlineReferenceCommentOperations myOperations;


  public InlineReferencesAutoPopupStrategy()
  {
    myOperations = LanguageManager.Instance.GetService<InlineReferenceCommentOperations>(Language);
  }
  
  
  public override bool AcceptsFile(IFile file, ITextControl textControl)
  {
    return this.MatchToken(file, textControl, node =>
    {
      if (NamesResolveUtil.TryFindAnyCommentNode(node) is not { }) return false;

      var caretOffset = textControl.Caret.DocumentOffset();
      return myOperations.TryExtractCompletionInlineReferenceInfo(node, caretOffset) is { };
    });
  }
}