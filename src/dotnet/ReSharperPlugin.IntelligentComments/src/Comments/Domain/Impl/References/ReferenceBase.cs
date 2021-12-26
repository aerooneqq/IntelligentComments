
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class ReferenceBase : IReference
{
  public string RawValue { get; }
  
  
  public ReferenceBase(string rawValue)
  {
    RawValue = rawValue;
  }
}