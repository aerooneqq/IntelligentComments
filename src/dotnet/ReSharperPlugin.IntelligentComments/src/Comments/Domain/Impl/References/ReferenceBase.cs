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

public record ResolveContextImpl([NotNull] ISolution Solution, [CanBeNull] IDocument Document) : IResolveContext;

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
      return new InvalidResolveResult($"Failed to resolve proxy reference with real reference id: {RealReferenceId}");
    }

    return realReference.Resolve(context);
  }
}