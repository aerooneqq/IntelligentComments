using IntelligentComments.Comments.Caches.Names;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Domain.Core.Content;
using IntelligentComments.Comments.Domain.Core.References;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.Rd.Util;
using JetBrains.ReSharper.Psi.Tree;
using NameWithKind = IntelligentComments.Comments.Calculations.Core.NameWithKind;

namespace IntelligentComments.Comments.Domain.Impl.References;

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
    NamesResolveUtil.ResolveName(new NameWithKind(Name, NameKind), context);

  public override void Print(PrettyPrinter printer)
  {
    using var _ = printer.IndentCookie();
    printer.Println($"{nameof(NamedEntityDomainReference)} with name {Name} and kind {NameKind}");
  }
}

public class NamedEntityDomainResolveResult : DomainResolveResult
{
  [CanBeNull] public IContentSegment ContentSegment { get; }
  [NotNull] public ITreeNode ParentCommentBlock { get; }
  public DocumentRange NameDeclarationDocumentRange { get; }
  public NameKind NameKind { get; }


  public NamedEntityDomainResolveResult(
    [CanBeNull] IContentSegment contentSegment, 
    [NotNull] ITreeNode parentCommentBlock, 
    DocumentRange nameDeclarationDocumentRange, 
    NameKind nameKind)
  {
    ContentSegment = contentSegment;
    ParentCommentBlock = parentCommentBlock;
    NameDeclarationDocumentRange = nameDeclarationDocumentRange;
    NameKind = nameKind;
  }
}