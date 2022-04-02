using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.Content;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using JetBrains.DocumentModel.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class InvariantDomainReference : DomainReferenceBase, IInvariantDomainReference
{
  public string InvariantName { get; }
  
  
  public InvariantDomainReference(string name) : base(name)
  {
    InvariantName = name;
  }


  public override DomainResolveResult Resolve(IDomainResolveContext context) => 
    InvariantResolveUtil.ResolveInvariantByName(InvariantName, context);
}

public class InvariantDomainResolveResult : DomainResolveResult
{
  [NotNull] public IInvariantContentSegment Invariant { get; }
  [NotNull] public IDocCommentBlock ParentDocCommentBlock { get; }
  public DocumentOffset InvariantDocumentOffset { get; }


  public InvariantDomainResolveResult(
    [NotNull] IInvariantContentSegment invariant, 
    [NotNull] IDocCommentBlock parentBlock, 
    DocumentOffset invariantDocumentOffset)
  {
    Invariant = invariant;
    ParentDocCommentBlock = parentBlock;
    InvariantDocumentOffset = invariantDocumentOffset;
  }
}