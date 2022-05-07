using IntelligentComments.Comments.Domain.Core.References;
using JetBrains.Annotations;
using JetBrains.Rd.Util;
using JetBrains.Util;

namespace IntelligentComments.Comments.Domain.Impl.References;

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

  public override void Print(PrettyPrinter printer)
  {
    using var _ = printer.IndentCookie();
    printer.Println($"{nameof(HttpDomainReference)} with name: {DisplayName}");
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


  public override void Print(PrettyPrinter printer)
  {
    using var _ = printer.IndentCookie();
    printer.Println($"{nameof(FileDomainReference)} with path: {Path}");
  }
}