using IntelligentComments.Comments.Calculations.Core.MultilineComments.HackComments;
using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace IntelligentComments.Comments.Calculations.Core.Languages.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpInlineHackCommentOperations : InlineHackCommentOperations
{
  public CSharpInlineHackCommentOperations([NotNull] ICommentsSettings settings) : base(settings)
  {
  }
}