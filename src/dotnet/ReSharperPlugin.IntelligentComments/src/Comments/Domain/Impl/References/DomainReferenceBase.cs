using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.Rd.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public abstract class DomainReferenceBase : IDomainReference
{
  public string RawValue { get; }


  protected DomainReferenceBase(string rawValue)
  {
    RawValue = rawValue;
  }
  
  
  public virtual DomainResolveResult Resolve(IDomainResolveContext context)
  {
    return EmptyDomainResolveResult.Instance;
  }

  public virtual void Print(PrettyPrinter printer)
  {
    using var _ = printer.IndentCookie();
    printer.Println($"{GetType().Name} with raw value: {RawValue}");
  }
}

public record DomainResolveContextImpl([NotNull] ISolution Solution, [CanBeNull] IDocument Document) : IDomainResolveContext;

public class ProxyDomainReference : DomainReferenceBase, IProxyDomainReference
{
  public int RealReferenceId { get; }


  public ProxyDomainReference(int realReferenceId, string rawValue) : base(rawValue)
  {
    RealReferenceId = realReferenceId;
  }


  public override DomainResolveResult Resolve(IDomainResolveContext context)
  {
    if (context.Document is not { } document) return EmptyDomainResolveResult.Instance;
    
    var cache = context.Solution.GetComponent<ReferencesCache>();
    if (cache.TryGetValue(document, RealReferenceId)?.DomainReference is not { } realReference)
    {
      return new InvalidDomainResolveResult($"Failed to resolve proxy reference with real reference id: {RealReferenceId}");
    }

    return realReference.Resolve(context);
  }

  public override void Print(PrettyPrinter printer)
  {
    using var _ = printer.IndentCookie();
    printer.Println($"Proxy reference with real id: {RealReferenceId}");
  }
}