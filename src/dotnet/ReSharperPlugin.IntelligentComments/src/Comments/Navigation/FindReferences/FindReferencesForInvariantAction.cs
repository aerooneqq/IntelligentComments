using JetBrains.Annotations;
using JetBrains.Application.DataContext;
using JetBrains.Application.UI.Actions;
using JetBrains.Application.UI.ActionsRevised.Menu;
using JetBrains.ReSharper.Feature.Services.Navigation.ContextNavigation;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation.FindReferences;

[Action(Id, Text)]
public class FindReferencesForInvariantAction : ContextNavigationActionBase<FindReferencesForInvariantNavigationProvider>
{
  [NotNull] public const string Text = "Find References To Invariant";
  [NotNull] public const string Id = nameof(FindReferencesForInvariantAction);

  public override bool Update(IDataContext context, ActionPresentation presentation, DelegateUpdate nextUpdate)
  {
    return NavigationUtil.TryExtractNameFromNamedEntity(context) is { };
  }

  public override void Execute(IDataContext dataContext, DelegateExecute nextExecute)
  {
    NavigationUtil.FindReferencesToInvariant(dataContext);
  }
}