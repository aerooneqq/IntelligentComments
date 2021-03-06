using System.Collections.Generic;
using IntelligentComments.Comments.PSI.DeclaredElements;
using IntelligentComments.Comments.PSI.Features.Navigation;
using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.Application;
using JetBrains.Application.DataContext;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel.DataContext;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.DataContext;

namespace IntelligentComments.Comments.PSI.Features.DataRules;

[ShellComponent]
public class DeclaredElementsDataRule
{
  [NotNull] private readonly ICommentsSettings mySettings;

  
  public DeclaredElementsDataRule(Lifetime lifetime, [NotNull] DataContexts dataContexts, [NotNull] ICommentsSettings settings)
  {
    mySettings = settings;
    dataContexts.RegisterDataRule(lifetime, "NamedEntitiesDeclaredElement", PsiDataConstants.DECLARED_ELEMENTS, AddNamedEntitiesElements);
  }

  
  [CanBeNull]
  private ICollection<IDeclaredElement> AddNamedEntitiesElements(IDataContext dataContext)
  {
    if (dataContext.GetData(ProjectModelDataConstants.SOLUTION) is not { } solution ||
        NavigationUtil.TryExtractNameFromNamedEntity(dataContext) is not { } extraction ||
        !mySettings.ExperimentalFeaturesEnabled.Value)
    {
      return dataContext.GetData(PsiDataConstants.DECLARED_ELEMENTS);
    } 

    var declaredElement = new NamedEntityDeclaredElement(solution, extraction.NameWithKind, extraction.DocumentRange);
    return new List<IDeclaredElement>() { declaredElement };
  }
}