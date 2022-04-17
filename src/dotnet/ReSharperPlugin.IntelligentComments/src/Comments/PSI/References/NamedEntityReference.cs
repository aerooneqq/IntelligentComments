using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.ExtensionsAPI.Resolve;
using JetBrains.ReSharper.Psi.JavaScript.Resolve;
using JetBrains.ReSharper.Psi.Resolve;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;
using ReSharperPlugin.IntelligentComments.Comments.PSI.DeclaredElements;
using ReSharperPlugin.IntelligentComments.Comments.PSI.Features.Rename;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.References;

public interface INamedEntityReference : IReference
{
  NameWithKind NameWithKind { get; }
}

public class NamedEntityReference : CheckedReferenceBase<ITreeNode>, INamedEntityReference
{
  private readonly TreeTextRange myRange;
  private readonly DocumentRange myDocumentRange;


  public NameWithKind NameWithKind { get; }

  
  public NamedEntityReference(
    [NotNull] ITreeNode owner, NameWithKind nameWithKind, TreeTextRange range, DocumentRange documentRange) 
    : base(owner)
  {
    NameWithKind = nameWithKind;
    myRange = range;
    myDocumentRange = documentRange;
  }

  
  public override ResolveResultWithInfo ResolveWithoutCache()
  {
    if (myOwner.GetSourceFile() is not { } sourceFile)
      return new ResolveResultWithInfo(EmptyResolveResult.Instance, ResolveErrorType.NOT_RESOLVED);

    var solution = sourceFile.GetSolution();
    var domainResolveContext = new DomainResolveContextImpl(solution, sourceFile.Document);
    if (NamesResolveUtil.ResolveName(NameWithKind, domainResolveContext) is not NamedEntityDomainResolveResult result)
    {
      return new ResolveResultWithInfo(EmptyResolveResult.Instance, ResolveErrorType.NOT_RESOLVED);
    }
    
    var declaredElement = new NamedEntityDeclaredElement(solution, NameWithKind, result.NameDeclarationDocumentRange);
    return new ResolveResultWithInfo(new SimpleResolveResult(declaredElement), ResolveErrorType.OK);
  }

  public override ISymbolTable GetReferenceSymbolTable(bool useReferenceName)
  {
    return EmptySymbolTable.INSTANCE;
  }
  
  public override string GetName() => NameWithKind.Name;
  public override TreeTextRange GetTreeTextRange() => myRange;
  public override IReference BindTo(IDeclaredElement element)
  {
    if (element is not NamedEntityDeclaredElement newDeclaredElement) return this;
    
    switch (myOwner)
    {
      case IDocCommentBlock docComment:
      {
        if (RenameUtil.FindAttributeValueToken(docComment, myDocumentRange) is not { } valueToken) return this;
        RenameUtil.ReplaceAttributeValue(valueToken, newDeclaredElement.NameWithKind.Name);
        break;
      }

      case ICommentNode commentNode:
      {
        RenameUtil.ReplaceReferenceCommentNode(commentNode, NameWithKind, newDeclaredElement.NameWithKind.Name);
        break;
      }
    }

    return this;
  }

  public override IReference BindTo(IDeclaredElement element, ISubstitution substitution) => this;
  public override IAccessContext GetAccessContext() => new DefaultAccessContext(myOwner);
  public override ISymbolFilter[] GetSymbolFilters() => EmptyArray<ISymbolFilter>.Instance;
}