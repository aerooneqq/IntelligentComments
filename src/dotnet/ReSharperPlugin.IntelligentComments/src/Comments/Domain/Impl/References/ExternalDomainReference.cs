using JetBrains.Annotations;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public abstract class ExternalDomainReference : DomainReferenceBase, IExternalDomainReference
{
  protected ExternalDomainReference(string rawValue) : base(rawValue)
  {
  }
}

public class HttpDomainReference : ExternalDomainReference, IHttpDomainReference
{
  public string DisplayName { get; }
  
  
  public HttpDomainReference([NotNull] string displayName, [NotNull] string rawLink) : base(rawLink)
  {
    DisplayName = displayName;
  }


  public override DomainResolveResult Resolve(IDomainResolveContext context)
  {
    return new DomainWebResourceResolveResult(RawValue);
  }
}

public class DomainWebResourceResolveResult : DomainResolveResult
{
  [NotNull] public string Link { get; }
  
  
  public DomainWebResourceResolveResult([NotNull] string link)
  {
    Link = link;
  }
}

public class FileDomainReference : ExternalDomainReference, IFileDomainReference
{
  public FileSystemPath Path { get; }

  
  public FileDomainReference(FileSystemPath path) : base(path.FullPath)
  {
    Path = path;
  }
}