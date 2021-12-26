using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class CodeEntityReference : ReferenceBase, ICodeEntityReference
{
  public CodeEntityReference(string rawMemberName) : base(rawMemberName)
  {
  }
}

public class LangWordReference : ReferenceBase, ILangWordReference
{
  public LangWordReference(string rawLangWord) : base(rawLangWord)
  {
  }
}