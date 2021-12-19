using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class CodeEntityReference : ReferenceBase, ICodeEntityReference
{
  public string RawMemberName { get; }
  

  public CodeEntityReference(string rawMemberName)
  {
    RawMemberName = rawMemberName;
  }
}