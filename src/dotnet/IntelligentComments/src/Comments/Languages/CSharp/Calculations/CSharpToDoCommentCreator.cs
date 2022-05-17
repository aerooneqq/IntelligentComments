using IntelligentComments.Comments.Calculations.Core.MultilineComments.ToDoComments;
using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace IntelligentComments.Comments.Languages.CSharp.Calculations;

[Language(typeof(CSharpLanguage))]
public class CSharpInlineToDoCommentOperations : InlineToDoCommentOperations
{
  public CSharpInlineToDoCommentOperations([NotNull] ICommentsSettings settings) : base(settings)
  {
  }
}