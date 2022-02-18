using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class ExternalReference : ReferenceBase, IExternalReference
{
  public ExternalReference(string rawValue) : base(rawValue)
  {
  }
}

public class HttpReference : ExternalReference, IHttpReference
{
  public HttpReference(string rawLink) : base(rawLink)
  {
  }
}

public class FileReference : ExternalReference, IFileReference
{
  public FileSystemPath Path { get; }

  
  public FileReference(FileSystemPath path) : base(path.FullPath)
  {
    Path = path;
  }
}