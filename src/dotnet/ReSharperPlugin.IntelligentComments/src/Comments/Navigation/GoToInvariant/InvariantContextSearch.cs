using JetBrains.Application;
using JetBrains.Application.DataContext;
using JetBrains.ReSharper.Feature.Services.Navigation;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation.GoToInvariant;

[ShellFeaturePart]
public class InvariantContextSearch : IContextSearch
{
  public bool IsAvailable(IDataContext dataContext)
  {
    return true;
  }

  public bool IsContextApplicable(IDataContext dataContext)
  {
    return GoToInvariantUtil.TryExtractInvariantNameFrom(dataContext) is { };
  }
}