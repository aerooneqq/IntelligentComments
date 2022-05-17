using IntelligentComments.Comments.Calculations.Core.MultilineComments.Invariants;
using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace IntelligentComments.Comments.Languages.CSharp.Calculations;

[Language(typeof(CSharpLanguage))]
public class CSharpInlineInvariantCommentBuilder : InlineInvariantCommentOperations
{
  public CSharpInlineInvariantCommentBuilder([NotNull] ICommentsSettings settings) : base(settings)
  {
  }
}