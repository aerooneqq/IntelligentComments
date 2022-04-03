using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.MultilineComments.ToDoComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.Languages.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpToDoCommentBuilder : ToDoCommentBuilder
{
}