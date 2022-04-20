using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Application;
using JetBrains.Application.DataContext;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel.DataContext;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.DataContext;
using JetBrains.Util.Collections;
using ReSharperPlugin.IntelligentComments.Comments.Navigation;
using ReSharperPlugin.IntelligentComments.Comments.PSI.DeclaredElements;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.Features.DataRules;

[ShellComponent]
public class DeclaredElementsDataRule
{
  public DeclaredElementsDataRule(Lifetime lifetime, [NotNull] DataContexts dataContexts)
  {
    dataContexts.RegisterDataRule(lifetime, "NamedEntitiesDeclaredElement", PsiDataConstants.DECLARED_ELEMENTS, AddNamedEntitiesElements);
  }

  private static ICollection<IDeclaredElement> AddNamedEntitiesElements(IDataContext dataContext)
  {
    if (dataContext.GetData(ProjectModelDataConstants.SOLUTION) is not { } solution ||
        NavigationUtil.TryExtractNameFromNamedEntity(dataContext) is not { } extraction)
    {
      return dataContext.GetData(PsiDataConstants.DECLARED_ELEMENTS);
    } 

    var declaredElement = new NamedEntityDeclaredElement(solution, extraction.NameWithKind, extraction.DocumentRange);
    return new List<IDeclaredElement>() { declaredElement };
  }
}