
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class ReferenceBase : IReference
{
  public string RawValue { get; }


  protected ReferenceBase(string rawValue)
  {
    RawValue = rawValue;
  }
  
  
  public virtual ResolveResult Resolve(IResolveContext context)
  {
    return EmptyResolveResult.Instance;
  }
}

public class ResolveContextImpl : IResolveContext
{
  [NotNull] public ISolution Solution { get; }

  
  public ResolveContextImpl([NotNull] ISolution solution)
  {
    Solution = solution;
  }
}