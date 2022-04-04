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
  public HttpDomainReference(string rawLink) : base(rawLink)
  {
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