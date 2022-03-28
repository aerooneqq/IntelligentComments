using JetBrains.Application;
using JetBrains.Application.DataContext;
using JetBrains.Application.UI.Actions;
using JetBrains.DocumentModel.DataContext;
using JetBrains.ProjectModel.DataContext;
using JetBrains.ReSharper.Feature.Services.Navigation.ContextNavigation;
using JetBrains.ReSharper.Feature.Services.Navigation.NavigationExtensions;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;
using System.Collections.Generic;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation.GoToInvariant;

[ContextNavigationProvider]
public class GoToInvariantNavigationProvider : ContextNavigationProviderBase<InvariantContextSearch>, INavigateFromHereProvider
{
  protected override NavigationActionGroup ActionGroup => NavigationActionGroup.Blessed;


  public GoToInvariantNavigationProvider(IFeaturePartsContainer manager) : base(manager)
  {
  }


  protected override string GetActionId(IDataContext dataContext) => GoToInvariantAction.Id;

  protected override string GetNavigationMenuTitle(IDataContext dataContext) => GoToInvariantAction.Text;

  protected override void Execute(
    IDataContext context, IEnumerable<InvariantContextSearch> searches, INavigationExecutionHost host)
  {
    if (GoToInvariantUtil.TryExtractInvariantNameFrom(context) is not { } name) return;
    if (context.GetData(ProjectModelDataConstants.SOLUTION) is not { } solution) return;
    if (context.GetData(DocumentModelDataConstants.DOCUMENT) is not { } document) return;

    var resolveContext = new ResolveContextImpl(solution, document);
    var resolveResult = InvariantResolveUtil.ResolveInvariantByName(name, resolveContext);
    if (resolveResult is not InvariantResolveResult invariantResolveResult) return;

    var sourceFile = invariantResolveResult.ParentDocCommentBlock.GetSourceFile();
    var offset = invariantResolveResult.InvariantDocumentOffset;

    sourceFile.Navigate(new TextRange(offset.Offset), true);
  }
}