
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class ReferenceBase : IReference
{
  public string RawValue { get; }


  protected ReferenceBase(string rawValue)
  {
    RawValue = rawValue;
  }
  
  
  public virtual ResolveResult Resolve()
  {
    return EmptyResolveResult.Instance;
  }
}