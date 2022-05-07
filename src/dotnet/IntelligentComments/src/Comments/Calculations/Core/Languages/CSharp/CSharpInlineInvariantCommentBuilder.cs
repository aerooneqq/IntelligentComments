using IntelligentComments.Comments.Calculations.Core.MultilineComments.Invariants;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace IntelligentComments.Comments.Calculations.Core.Languages.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpInlineInvariantCommentBuilder : InlineInvariantCommentOperations
{
}