using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface ICodeEntityReference : IReference
{
  new DeclaredElementResolveResult Resolve();
}

public class DeclaredElementResolveResult : ResolveResult
{
  [CanBeNull] public IDeclaredElement DeclaredElement { get; }


  public DeclaredElementResolveResult([CanBeNull] IDeclaredElement declaredElement)
  {
    DeclaredElement = declaredElement;
  }
}

public interface ILangWordReference : IReference
{
}