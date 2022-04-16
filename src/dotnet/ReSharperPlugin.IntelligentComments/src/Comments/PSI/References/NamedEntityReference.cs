using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.ExtensionsAPI.Resolve;
using JetBrains.ReSharper.Psi.JavaScript.Resolve;
using JetBrains.ReSharper.Psi.Resolve;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;
using ReSharperPlugin.IntelligentComments.Comments.PSI.DeclaredElements;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.References;

public class NamedEntityReference : CheckedReferenceBase<ICommentNode>
{
  private readonly NameWithKind myNameWithKind;
  private readonly TreeTextRange myRange;

  
  public NamedEntityReference([NotNull] ICommentNode owner, NameWithKind nameWithKind, TreeTextRange range) 
    : base(owner)
  {
    myNameWithKind = nameWithKind;
    myRange = range;
  }
  

  public override ResolveResultWithInfo ResolveWithoutCache()
  {
    if (myOwner.GetSourceFile() is not { } sourceFile)
      return new ResolveResultWithInfo(EmptyResolveResult.Instance, ResolveErrorType.NOT_RESOLVED);

    var solution = sourceFile.GetSolution();
    var domainResolveContext = new DomainResolveContextImpl(solution, sourceFile.Document);
    if (NamesResolveUtil.ResolveName(myNameWithKind, domainResolveContext) is not NamedEntityDomainResolveResult result)
    {
      return new ResolveResultWithInfo(EmptyResolveResult.Instance, ResolveErrorType.NOT_RESOLVED);
    }
    
    var declaredElement = new NamedEntityDeclaredElement(solution, myNameWithKind, result.NameDeclarationDocumentOffset);
    return new ResolveResultWithInfo(new SimpleResolveResult(declaredElement), ResolveErrorType.OK);
  }

  public override ISymbolTable GetReferenceSymbolTable(bool useReferenceName)
  {
    return new JsEmptySymbolTable();
  }
  
  
  public override string GetName() => myNameWithKind.Name;
  public override TreeTextRange GetTreeTextRange() => myRange;
  public override IReference BindTo(IDeclaredElement element) => this;
  public override IReference BindTo(IDeclaredElement element, ISubstitution substitution) => this;
  public override IAccessContext GetAccessContext() => new DefaultAccessContext(myOwner);
  public override ISymbolFilter[] GetSymbolFilters() => EmptyArray<ISymbolFilter>.Instance;
}