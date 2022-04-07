using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class NamedEntityDomainReference : DomainReferenceBase, INamedEntityDomainReference
{
  public string Name { get; }
  public NameKind NameKind { get; }


  public NamedEntityDomainReference([NotNull] string name, NameKind nameKind) : base(name)
  {
    Name = name;
    NameKind = nameKind;
  }


  public override DomainResolveResult Resolve(IDomainResolveContext context) => 
    NamesResolveUtil.ResolveName(Name, context, NameKind);
}

public class NamedEntityDomainResolveResult : DomainResolveResult
{
  [NotNull] public IContentSegment ContentSegment { get; }
  [NotNull] public IDocCommentBlock ParentDocCommentBlock { get; }
  public DocumentOffset InvariantDocumentOffset { get; }
  public NameKind NameKind { get; }


  public NamedEntityDomainResolveResult(
    [NotNull] IContentSegment contentSegment, 
    [NotNull] IDocCommentBlock parentBlock, 
    DocumentOffset invariantDocumentOffset, 
    NameKind nameKind)
  {
    ContentSegment = contentSegment;
    ParentDocCommentBlock = parentBlock;
    InvariantDocumentOffset = invariantDocumentOffset;
    NameKind = nameKind;
  }
}