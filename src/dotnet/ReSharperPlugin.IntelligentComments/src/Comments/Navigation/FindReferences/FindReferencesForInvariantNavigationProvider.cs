using JetBrains.Annotations;
using JetBrains.Application;
using JetBrains.Application.DataContext;
using JetBrains.Application.UI.TreeModels;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ProjectModel.DataContext;
using JetBrains.ReSharper.Feature.Services.Navigation.ContextNavigation;
using JetBrains.ReSharper.Feature.Services.Navigation.Descriptors;
using JetBrains.ReSharper.Feature.Services.Navigation.Requests;
using JetBrains.ReSharper.Feature.Services.Occurrences;
using JetBrains.ReSharper.Feature.Services.Tree;
using JetBrains.ReSharper.Feature.Services.Tree.SectionsManagement;
using JetBrains.ReSharper.Psi;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;
using System;
using System.Collections.Generic;
using System.Linq;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation.FindReferences;

[ContextNavigationProvider]
public class FindReferencesForInvariantNavigationProvider : ContextNavigationProviderBase<InvariantContextSearch>, INavigateFromHereProvider
{
  protected override NavigationActionGroup ActionGroup => NavigationActionGroup.Blessed;

  
  public FindReferencesForInvariantNavigationProvider(IFeaturePartsContainer manager) : base(manager)
  {
  }

  
  protected override string GetActionId(IDataContext dataContext) => FindReferencesForInvariantAction.Id;
  protected override string GetNavigationMenuTitle(IDataContext dataContext) => FindReferencesForInvariantAction.Text;

  protected override void Execute(IDataContext dataContext, IEnumerable<InvariantContextSearch> searches, INavigationExecutionHost host)
  {
    NavigationUtil.FindReferencesToInvariant(dataContext, host);
  }
}

internal class MyOccurrencesBrowserDescriptor : OccurrenceBrowserDescriptor
{
  public override TreeModel Model { get; } = new TreeSimpleModel();

  
  public MyOccurrencesBrowserDescriptor([NotNull] ISolution solution) : base(solution)
  {
  }
}

internal class MySearchOccurrenceBrowserDescriptor : SearchDescriptor
{
  public MySearchOccurrenceBrowserDescriptor(SearchRequest request) : base(request)
  {
  }
  

  public override string GetResultsTitle(OccurrenceSection section)
  {
    return "References which reference this invariant";
  }

  protected override Func<SearchRequest, IOccurrenceBrowserDescriptor> GetDescriptorFactory()
  {
    return request => new MyOccurrencesBrowserDescriptor(request.Solution);
  }
}

public class InvariantReferenceOccurence : TextOccurrence
{
  public InvariantReferenceOccurence(IPsiSourceFile sourceFile, DocumentOffset offset) 
    : base(sourceFile, new DocumentRange(offset), OccurrencePresentationOptions.DefaultOptions)
  {
  }
}