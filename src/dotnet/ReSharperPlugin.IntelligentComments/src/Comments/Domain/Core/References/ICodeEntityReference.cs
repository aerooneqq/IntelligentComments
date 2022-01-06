using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.Rider.Model;
using JetBrains.Util;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface ICodeEntityReference : IReference
{
}

public interface IXmlDocCodeEntityReference : ICodeEntityReference
{
}

public interface ISandBoxCodeEntityReference : ICodeEntityReference
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

public interface ILangWordReference : IReference
{
}