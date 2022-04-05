using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Errors;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.Languages.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpCommentProblemsCollector : CommentProblemsCollectorBase
{
}