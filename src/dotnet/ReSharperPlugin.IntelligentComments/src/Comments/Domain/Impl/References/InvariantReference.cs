using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class InvariantReference : ReferenceBase, IInvariantReference
{
  public string InvariantName { get; }
  
  
  public InvariantReference(string name) : base(name)
  {
    InvariantName = name;
  }
}