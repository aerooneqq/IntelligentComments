using JetBrains.Annotations;
using JetBrains.Application.DataContext;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel.DataContext;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.TextControl.DataContext;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;
using ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation.GoToInvariant;

internal static class GoToInvariantUtil
{
  [CanBeNull]
  public static string TryExtractInvariantNameFrom([NotNull] IDataContext dataContext)
  {
    if (dataContext.GetData(TextControlDataConstants.TEXT_CONTROL) is not { } editor) return null;
    if (dataContext.GetData(ProjectModelDataConstants.SOLUTION) is not { } solution) return null;
    if (editor.Document.GetPsiSourceFile(solution)?.GetPrimaryPsiFile() is not ICSharpFile psiFile) return null;

    var docOffset = editor.Caret.Position.Value.ToDocOffset();
    var caretDocumentOffset = new DocumentOffset(editor.Document, (int)docOffset);
    var range = psiFile.Translate(new DocumentRange(caretDocumentOffset));
    var token = psiFile.FindTokenAt(range.StartOffset);

    if (token?.TryFindDocCommentBlock() is not { } docCommentBlock) return null;
    if (docCommentBlock.TryGetXmlToken(caretDocumentOffset) is not IXmlAttributeValue { Parent: { } parent } value)
    {
      return null;
    }

    if (parent is not IXmlAttribute { AttributeName: CommentsBuilderUtil.InvariantReferenceSourceAttrName }) 
      return null;
    
    return value.UnquotedValue;
  }
}