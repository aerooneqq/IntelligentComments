using IntelligentComments.Comments.Calculations.Core.DocComments.Errors;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace IntelligentComments.Comments.Calculations.Core.Languages.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpDocCommentProblemsCollector : DocCommentProblemsCollectorBase
{
}