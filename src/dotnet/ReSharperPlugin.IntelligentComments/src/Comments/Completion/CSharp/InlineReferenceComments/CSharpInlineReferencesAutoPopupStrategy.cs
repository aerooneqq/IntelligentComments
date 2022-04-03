using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.TextControl;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.InlineReferenceComments;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

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
      if (InvariantResolveUtil.TryFindAnyCommentNode(node) is not { } commentNode) return false;

      var caretOffset = new DocumentOffset(textControl.Document, (int)textControl.Caret.Position.Value.ToDocOffset());
      if (myCreator.TryExtractCompletionInlineReferenceInfo(node, caretOffset) is not { }) return false;
      
      return true;
    });
  }
}