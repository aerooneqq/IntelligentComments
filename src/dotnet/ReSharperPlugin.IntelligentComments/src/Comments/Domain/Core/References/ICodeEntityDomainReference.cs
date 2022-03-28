using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.Rider.Model;
using JetBrains.Util;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface ICodeEntityDomainReference : IDomainReference
{
}

public interface IXmlDocCodeEntityDomainReference : ICodeEntityDomainReference
{
}

public interface ISandBoxCodeEntityDomainReference : ICodeEntityDomainReference
{
  public string SandboxDocumentId { get; }
  public IDocument OriginalDocument { get; }
  public TextRange Range { get; }
}

public class DeclaredElementResolveResult : ResolveResult
{
  [CanBeNull] public IDeclaredElement DeclaredElement { get; }


  public DeclaredElementResolveResult([CanBeNull] IDeclaredElement declaredElement)
  {
    DeclaredElement = declaredElement;
  }
}

public interface ILangWordDomainReference : IDomainReference
{
}