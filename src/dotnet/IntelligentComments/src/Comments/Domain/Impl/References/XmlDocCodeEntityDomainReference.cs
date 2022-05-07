using IntelligentComments.Comments.Caches;
using IntelligentComments.Comments.Domain.Core.References;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Modules;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Util;
using JetBrains.Util;

namespace IntelligentComments.Comments.Domain.Impl.References;

public class XmlDocCodeEntityDomainReference : DomainReferenceBase, IXmlDocCodeEntityDomainReference
{
  [NotNull] private readonly IPsiServices myServices;
  [NotNull] private readonly IPsiModule myModule;

  
  public XmlDocCodeEntityDomainReference(
    [CanBeNull] string rawMemberName, 
    [NotNull] IPsiServices services, 
    [NotNull] IPsiModule module) 
    : base(rawMemberName)
  {
    myServices = services;
    myModule = module;
  }
  
  
  public override DomainResolveResult Resolve(IDomainResolveContext context)
  {
    var declaredElement = XMLDocUtil.ResolveId(myServices, RawValue, myModule, true);
    return new DeclaredElementDomainResolveResult(declaredElement);
  }
}

public class SandBoxCodeEntityDomainReference : DomainReferenceBase, ISandBoxCodeEntityDomainReference
{
  [CanBeNull] private readonly IDeclaredElement myAlreadyResolvedElement;
  
  
  public string SandboxDocumentId { get; }
  public IDocument OriginalDocument { get; }
  public TextRange Range { get; }
  
  
  public SandBoxCodeEntityDomainReference(
    [NotNull] string rawValue,
    [NotNull] string sandboxDocumentId,
    [NotNull] IDocument document,
    TextRange range,
    [CanBeNull] IDeclaredElement alreadyResolvedElement = null)
    : base(rawValue)
  {
    myAlreadyResolvedElement = alreadyResolvedElement;
    Range = range;
    SandboxDocumentId = sandboxDocumentId;
    OriginalDocument = document;
  }

  
  public override DomainResolveResult Resolve(IDomainResolveContext context)
  {
    if (myAlreadyResolvedElement is { }) return new DeclaredElementDomainResolveResult(myAlreadyResolvedElement);

    var solution = context.Solution;
    var sourceFile = solution.TryGetComponent<ISandboxesCache>()?.TryGetSandboxPsiSourceFile(OriginalDocument, SandboxDocumentId);
    if (sourceFile is null) return new InvalidDomainResolveResult("Failed to get source files for document id");
    
    var range = new TreeTextRange(new TreeOffset(Range.StartOffset), new TreeOffset(Range.EndOffset));
    var node = sourceFile?.GetPrimaryPsiFile()?.FindNodeAt(range);
    var declaredElement = node?.Parent?.GetReferences().FirstOrDefault()?.Resolve().DeclaredElement;
    return new DeclaredElementDomainResolveResult(declaredElement);
  }
}

public class LangWordDomainReference : DomainReferenceBase, ILangWordDomainReference
{
  public LangWordDomainReference(string rawLangWord) : base(rawLangWord)
  {
  }
}