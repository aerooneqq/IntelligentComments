using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using ReSharperPlugin.IntelligentComments.Comments.Caches;
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

public class ProxyReference : ReferenceBase, IProxyReference
{
  public int RealReferenceId { get; }


  public ProxyReference(int realReferenceId) : base(string.Empty)
  {
    RealReferenceId = realReferenceId;
  }
  
  
  public IReference GetRealReference(ISolution solution, IDocument contextDocument)
  {
    return solution.GetComponent<ReferencesCache>()[contextDocument, RealReferenceId]?.Reference;
  }
}