using JetBrains.Application;
using JetBrains.Application.DataContext;
using JetBrains.ReSharper.Feature.Services.Navigation.ContextNavigation;
using System.Collections.Generic;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation.GoToInvariant;

[ContextNavigationProvider]
public class GoToInvariantNavigationProvider : ContextNavigationProviderBase<InvariantReferenceContextSearch>, INavigateFromHereProvider
{
  protected override NavigationActionGroup ActionGroup => NavigationActionGroup.Blessed;


  public GoToInvariantNavigationProvider(IFeaturePartsContainer manager) : base(manager)
  {
  }


  protected override string GetActionId(IDataContext dataContext) => GoToInvariantAction.Id;

  protected override string GetNavigationMenuTitle(IDataContext dataContext) => GoToInvariantAction.Text;

  protected override void Execute(
    IDataContext context, IEnumerable<InvariantReferenceContextSearch> searches, INavigationExecutionHost host)
  {
    NavigationUtil.NavigateToInvariantIfFound(context, host);
  }
}