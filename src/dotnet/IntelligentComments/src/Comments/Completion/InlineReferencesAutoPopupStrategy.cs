using IntelligentComments.Comments.Caches.Names;
using IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;
using IntelligentComments.Comments.Languages.CSharp.Completion;
using JetBrains.Annotations;
using JetBrains.Application.Components;
using JetBrains.Application.Parts;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.TextControl;

namespace IntelligentComments.Comments.Completion;

[SolutionComponent(Instantiation.ContainerAsyncPrimaryThread)]
public class InlineReferencesAutoPopupStrategy : CSharpAutoPopupStrategyBase
{
  public override bool AcceptsFile(IFile file, ITextControl textControl)
  {
    return this.MatchToken(file, textControl, node =>
    {
      if (NamesResolveUtil.TryFindAnyCommentNode(node) is not { }) return false;

      var caretOffset = textControl.Caret.DocumentOffset();
      var operations = LanguageManager.Instance.GetService<InlineReferenceCommentOperations>(Language);
      return operations.TryExtractCompletionInlineReferenceInfo(node, caretOffset) is { };
    });
  }
}