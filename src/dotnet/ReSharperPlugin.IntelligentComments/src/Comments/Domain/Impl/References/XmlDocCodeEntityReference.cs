using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Modules;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Util;
using JetBrains.Rider.Model;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches;
using ReSharperPlugin.IntelligentComments.Comments.CodeFragmentsHighlighting;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

public class XmlDocCodeEntityReference : ReferenceBase, IXmlDocCodeEntityReference
{
  [NotNull] private readonly IPsiServices myServices;
  [NotNull] private readonly IPsiModule myModule;

  
  public XmlDocCodeEntityReference(
    [CanBeNull] string rawMemberName, 
    [NotNull] IPsiServices services, 
    [NotNull] IPsiModule module) 
    : base(rawMemberName)
  {
    myServices = services;
    myModule = module;
  }
  
  
  public new DeclaredElementResolveResult Resolve(IResolveContext context)
  {
    var declaredElement = XMLDocUtil.ResolveId(myServices, RawValue, myModule, true);
    return new DeclaredElementResolveResult(declaredElement);
  }
}

public class SandBoxCodeEntityReference : ReferenceBase, ISandBoxCodeEntityReference
{
  [CanBeNull] private readonly IDeclaredElement myAlreadyResolvedElement;
  
  
  public string SandboxDocumentId { get; }
  public RdDocumentId OriginalDocumentId { get; }
  public TextRange Range { get; }
  
  
  public SandBoxCodeEntityReference(
    [NotNull] string rawValue,
    [NotNull] string sandboxDocumentId,
    [NotNull] RdDocumentId documentId,
    TextRange range,
    [CanBeNull] IDeclaredElement alreadyResolvedElement = null)
    : base(rawValue)
  {
    myAlreadyResolvedElement = alreadyResolvedElement;
    Range = range;
    SandboxDocumentId = sandboxDocumentId;
    OriginalDocumentId = documentId;
  }

  
  public new DeclaredElementResolveResult Resolve(IResolveContext context)
  {
    if (myAlreadyResolvedElement is { }) return new DeclaredElementResolveResult(myAlreadyResolvedElement);

    var solution = context.Solution;
    var originalDocument = DocumentHostBase.GetInstance(solution).TryGetHostDocument(OriginalDocumentId);
    var sourceFile = solution.GetComponent<SandboxesCache>().TryGetSandboxPsiSourceFile(originalDocument, SandboxDocumentId);

    var range = new TreeTextRange(new TreeOffset(Range.StartOffset), new TreeOffset(Range.EndOffset));
    var node = sourceFile?.GetPrimaryPsiFile()?.FindNodeAt(range);
    var declaredElement = node?.Parent?.GetReferences().FirstOrDefault()?.Resolve().DeclaredElement;
    return new DeclaredElementResolveResult(declaredElement);
  }
}

public class LangWordReference : ReferenceBase, ILangWordReference
{
  public LangWordReference(string rawLangWord) : base(rawLangWord)
  {
  }
}