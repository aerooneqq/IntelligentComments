using JetBrains.Annotations;
using JetBrains.Application.UI.ActionsRevised.Menu;
using JetBrains.ReSharper.Feature.Services.Navigation.ContextNavigation;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation.GoToInvariant;

[Action(Id, Text)]
public class GoToInvariantAction : ContextNavigationActionBase<GoToInvariantNavigationProvider>
{
  [NotNull] public const string Text = "Go To Invariant";
  [NotNull] public const string Id = "GoToInvariantAction";
}