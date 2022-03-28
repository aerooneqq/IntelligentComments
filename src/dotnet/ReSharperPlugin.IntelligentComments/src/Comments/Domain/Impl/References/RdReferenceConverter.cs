using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.RdBackend.Common.Features.Util.Ranges;
using JetBrains.ReSharper.Psi;
using JetBrains.Rider.Backend.Features.Documents;
using JetBrains.Rider.Backend.Features.TextControls;
using JetBrains.Rider.Model;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

[SolutionComponent]
public class RdReferenceConverter
{
  [NotNull] private readonly ISolution mySolution;
  [NotNull] private readonly RiderTextControlHost myTextControlHost;
  [NotNull] private readonly IPsiServices myPsiServices;
  [NotNull] private readonly DocumentHostBase myDocumentHostBase;


  public RdReferenceConverter(
    [NotNull] ISolution solution,
    [NotNull] RiderTextControlHost textControlHost,
    [NotNull] IPsiServices psiServices,
    [NotNull] RiderDocumentHost documentHost)
  {
    mySolution = solution;
    myTextControlHost = textControlHost;
    myPsiServices = psiServices;
    myDocumentHostBase = documentHost;
  }


  [CanBeNull] public IDomainReference TryGetReference([NotNull] RdReferenceResolveRequest request) =>
    TryGetReference(request.Reference, request.TextControlId);

  [CanBeNull]
  public IDomainReference TryGetReference([NotNull] RdReference reference, [NotNull] TextControlId textControlId)
  {
    return reference switch
    {
      RdProxyReference proxyReference => new ProxyDomainReference(proxyReference.RealReferenceId),
      RdXmlDocCodeEntityReference xmlReference => TryGetXmlDocReference(textControlId, xmlReference),
      RdSandboxCodeEntityReference sandBoxReference => TryGetSandboxReference(sandBoxReference),
      RdInvariantReference invariantReference => new InvariantDomainReference(invariantReference.InvariantName),
      _ => null
    };
  }
  
  [CanBeNull]
  private IXmlDocCodeEntityDomainReference TryGetXmlDocReference(
    [NotNull] TextControlId textControlId,
    [NotNull] RdXmlDocCodeEntityReference reference)
  {
    if (myTextControlHost.TryGetTextControl(textControlId) is not { } textControl)
    {
      return null;
    }
    
    var document = textControl.Document;
    if (document.GetPsiSourceFile(mySolution) is not { } sourceFile)
    {
      return null;
    }
    
    var module = sourceFile.PsiModule;
    var rawName = reference.RawValue;
    return new XmlDocCodeEntityDomainReference(rawName, myPsiServices, module);
  }
  
  [CanBeNull]
  private ISandBoxCodeEntityDomainReference TryGetSandboxReference([NotNull] RdSandboxCodeEntityReference reference)
  {
    if (reference.OriginalDocumentId is null) return null;
    
    var document = myDocumentHostBase.TryGetHostDocument(reference.OriginalDocumentId);
    if (document is null) return null;

    return new SandBoxCodeEntityDomainReference(
      reference.RawValue, reference.SandboxFileId, document, reference.Range.ToTextRange());
  }
}