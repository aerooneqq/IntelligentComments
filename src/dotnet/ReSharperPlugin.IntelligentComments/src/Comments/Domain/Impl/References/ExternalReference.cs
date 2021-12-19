using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class ExternalReference : ReferenceBase, IExternalReference
{
  
}

public class HttpReference : ExternalReference, IHttpReference
{
  public string RawLink { get; }


  public HttpReference(string rawLink)
  {
    RawLink = rawLink;
  }
}