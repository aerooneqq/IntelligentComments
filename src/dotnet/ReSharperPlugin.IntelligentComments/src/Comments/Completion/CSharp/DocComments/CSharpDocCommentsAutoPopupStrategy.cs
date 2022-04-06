using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.TextControl;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;

[SolutionComponent]
public class CSharpDocCommentsAutoPopupStrategy : CSharpAutoPopupStrategyBase
{
  public override bool AcceptsFile(IFile file, ITextControl textControl)
  {
    return this.MatchToken(file, textControl, node =>
    {
      if (node.TryFindDocCommentBlock() is not { } docComment) return false;
      var offset = textControl.Caret.DocumentOffset();
      var token = docComment.TryGetXmlToken(offset);
      if (token is not IXmlValueToken { Parent: IXmlAttribute parent }) return false;

      return DocCommentsBuilderUtil.PossibleReferenceTagAttributes.Contains(parent.AttributeName) ||
             parent.AttributeName == DocCommentsBuilderUtil.InvariantNameAttrName;
    });
  }
}