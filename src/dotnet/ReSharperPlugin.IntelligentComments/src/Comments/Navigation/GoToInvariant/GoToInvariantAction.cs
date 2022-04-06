using JetBrains.Annotations;
using JetBrains.Application.DataContext;
using JetBrains.Application.UI.Actions;
using JetBrains.Application.UI.ActionsRevised.Menu;
using JetBrains.ReSharper.Feature.Services.Navigation.ContextNavigation;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation.GoToInvariant;

[Action(Id, Text)]
public class GoToInvariantAction : ContextNavigationActionBase<GoToInvariantNavigationProvider>
{
  [NotNull] public const string Text = "Go To Invariant";
  [NotNull] public const string Id = nameof(GoToInvariantAction);

  public override bool Update(IDataContext context, ActionPresentation presentation, DelegateUpdate nextUpdate)
  {
    return NavigationUtil.TryExtractNameFromReference(context) is { };
  }

  public override void Execute(IDataContext dataContext, DelegateExecute nextExecute)
  {
    NavigationUtil.NavigateToInvariantIfFound(dataContext);
  }
}