using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Modules;
using JetBrains.ReSharper.Psi.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class CodeEntityReference : ReferenceBase, ICodeEntityReference
{
  [NotNull] private readonly IPsiServices myServices;
  [NotNull] private readonly IPsiModule myModule;

  
  public CodeEntityReference(
    [CanBeNull] string rawMemberName, 
    [NotNull] IPsiServices services, 
    [NotNull] IPsiModule module) 
    : base(rawMemberName)
  {
    myServices = services;
    myModule = module;
  }
  
  
  public new DeclaredElementResolveResult Resolve()
  {
    var declaredElement = XMLDocUtil.ResolveId(myServices, RawValue, myModule, true);
    return new DeclaredElementResolveResult(declaredElement);
  }
}

public class LangWordReference : ReferenceBase, ILangWordReference
{
  public LangWordReference(string rawLangWord) : base(rawLangWord)
  {
  }
}