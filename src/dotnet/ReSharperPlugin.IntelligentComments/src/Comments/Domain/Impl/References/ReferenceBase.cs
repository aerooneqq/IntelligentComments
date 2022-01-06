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
  public ISolution Solution { get; }
  public IDocument Document { get; }


  public ResolveContextImpl([NotNull] ISolution solution, [CanBeNull] IDocument document)
  {
    Solution = solution;
    Document = document;
  }
}

public class ProxyReference : ReferenceBase, IProxyReference
{
  public int RealReferenceId { get; }


  public ProxyReference(int realReferenceId) : base(string.Empty)
  {
    RealReferenceId = realReferenceId;
  }


  public override ResolveResult Resolve(IResolveContext context)
  {
    if (context.Document is not { } document) return EmptyResolveResult.Instance;
    
    var cache = context.Solution.GetComponent<ReferencesCache>();
    if (cache.TryGetValue(document, RealReferenceId)?.Reference is not { } realReference)
    {
      return EmptyResolveResult.Instance;
    }

    return realReference.Resolve(context);
  }
}