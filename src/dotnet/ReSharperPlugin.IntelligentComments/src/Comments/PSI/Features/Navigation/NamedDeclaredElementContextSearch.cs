using System;
using System.Linq;
using JetBrains.Application;
using JetBrains.Application.DataContext;
using JetBrains.ReSharper.Feature.Services.Navigation.ContextNavigation;
using JetBrains.ReSharper.Feature.Services.Navigation.Requests;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.DataContext;
using ReSharperPlugin.IntelligentComments.Comments.PSI.DeclaredElements;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.Features.Navigation;

[ShellFeaturePart]
public class NamedDeclaredElementContextSearch : DefaultDeclarationSearch
{
  public override bool IsGotoDeclarationApplicable(IDeclaredElement declaredElement)
  {
    return declaredElement is NamedEntityDeclaredElement;
  }

  public override bool IsContextApplicable(IDataContext dataContext)
  {
    if (dataContext.GetData(PsiDataConstants.DECLARED_ELEMENTS) is not { } elements) return false;

    return elements.Any(element => element is NamedEntityDeclaredElement);
  }

  protected override SearchDeclarationsRequest GetDeclarationSearchRequest(
    DeclaredElementTypeUsageInfo elementInfo, 
    Func<bool> checkCancelled)
  {
    return new NamedEntitiesSearchRequest(elementInfo);
  }
}