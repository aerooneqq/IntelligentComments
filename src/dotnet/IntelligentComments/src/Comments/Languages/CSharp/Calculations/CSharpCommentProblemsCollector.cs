using IntelligentComments.Comments.Calculations.Core.DocComments.Errors;
using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace IntelligentComments.Comments.Languages.CSharp.Calculations;

[Language(typeof(CSharpLanguage))]
public class CSharpDocCommentProblemsCollector : DocCommentProblemsCollectorBase
{
  public CSharpDocCommentProblemsCollector([NotNull] ICommentsSettings settings) : base(settings)
  {
  }
}