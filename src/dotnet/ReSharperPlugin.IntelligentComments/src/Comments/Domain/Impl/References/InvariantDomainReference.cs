using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Caches;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class InvariantDomainReference : DomainReferenceBase, IInvariantDomainReference
{
  public string InvariantName { get; }
  
  
  public InvariantDomainReference(string name) : base(name)
  {
    InvariantName = name;
  }


  public override DomainResolveResult Resolve(IDomainResolveContext context) => 
    NamesResolveUtil.ResolveName(InvariantName, context, NameKind.Invariant);
}

public class NamedEntityDomainResolveResult : DomainResolveResult
{
  [NotNull] public IContentSegment ContentSegment { get; }
  [NotNull] public IDocCommentBlock ParentDocCommentBlock { get; }
  public DocumentOffset InvariantDocumentOffset { get; }


  public NamedEntityDomainResolveResult(
    [NotNull] IContentSegment contentSegment, 
    [NotNull] IDocCommentBlock parentBlock, 
    DocumentOffset invariantDocumentOffset)
  {
    ContentSegment = contentSegment;
    ParentDocCommentBlock = parentBlock;
    InvariantDocumentOffset = invariantDocumentOffset;
  }
}