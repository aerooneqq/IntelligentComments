using System;
using JetBrains.Annotations;
using JetBrains.Application.StdApplicationUI;
using JetBrains.Application.Threading;
using JetBrains.Core;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.Rd.Tasks;
using JetBrains.RdBackend.Common.Features;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.RdBackend.Common.Features.Services;
using JetBrains.ReSharper.Feature.Services.Navigation;
using JetBrains.ReSharper.Feature.Services.Navigation.NavigationExtensions;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using JetBrains.Rider.Backend.Features.Documents;
using JetBrains.Rider.Model;
using JetBrains.Util;
using JetBrains.Util.Maths;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation;

[SolutionComponent]
public class CommentsNavigationHost
{
  private readonly Lifetime myLifetime;
  [NotNull] private readonly OpensUri myOpensUri;
  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly ISolution mySolution;
  [NotNull] private readonly IPersistentIndexManager myManager;
  [NotNull] private readonly IDeclaredElementNavigationService myNavigationService;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly RdReferenceConverter myRdReferenceConverter;
  [NotNull] private readonly DocumentHostBase myDocumentHostBase;


  public CommentsNavigationHost(
    Lifetime lifetime,
    [NotNull] OpensUri opensUri,
    [NotNull] ILogger logger,
    [NotNull] ISolution solution,
    [NotNull] IPersistentIndexManager manager,
    [NotNull] IDeclaredElementNavigationService navigationService,
    [NotNull] IShellLocks shellLocks,
    [NotNull] RdReferenceConverter rdReferenceConverter,
    [NotNull] RiderDocumentHost documentHost)
  {
    myLifetime = lifetime;
    myOpensUri = opensUri;
    myLogger = logger;
    mySolution = solution;
    myManager = manager;
    myNavigationService = navigationService;
    myShellLocks = shellLocks;
    myRdReferenceConverter = rdReferenceConverter;
    myDocumentHostBase = documentHost;
    
    solution.GetProtocolSolution().GetRdCommentsModel().PerformNavigation.Set(HandleNavigationRequest);
  }

  
  private RdTask<Unit> HandleNavigationRequest(Lifetime lifetime, RdNavigationRequest request)
  {
    var task = new RdTask<Unit>();

    myShellLocks.QueueReadLock(myLifetime, $"{nameof(CommentsNavigationHost)}::ServingRequest", () =>
    {
      using var _ = CompilationContextCookie.GetExplicitUniversalContextIfNotSet();
      switch (request)
      {
        case RdReferenceNavigationRequest referenceNavigationRequest:
        {
          PerformReferenceNavigation(referenceNavigationRequest);
          break;
        }
        case RdFileOffsetNavigationRequest offsetNavigationRequest:
        {
          PerformSourceFileNavigation(offsetNavigationRequest);
          break; 
        }
      }

      task.Set(Unit.Instance);
    });

    return task;
  }

  private void PerformSourceFileNavigation(RdFileOffsetNavigationRequest request)
  {
    var rdSourceFileId = request.SourceFileId;
    var id = new OWORD(rdSourceFileId.LWord, rdSourceFileId.HWord);
    var psiSourceFile = myManager[id];
    psiSourceFile.Navigate(new TextRange(request.Offset), true);
  }

  private void PerformReferenceNavigation(RdReferenceNavigationRequest request)
  {
    var (rdReference, textControlId) = request.ResolveRequest;
    if (myRdReferenceConverter.TryGetReference(rdReference, textControlId) is not { } reference)
    {
      myLogger.Warn($"Failed to get reference for {rdReference}");
      return;
    }

    if (myDocumentHostBase.TryGetHostDocument(textControlId.DocumentId) is not { } document)
    {
      myLogger.Warn($"Failed to get document for {textControlId}");
      return;
    }

    var resolveContext = new DomainResolveContextImpl(mySolution, document);
    var resolveResult = reference.Resolve(resolveContext);

    switch (resolveResult)
    {
      case DeclaredElementDomainResolveResult { DeclaredElement: { } declaredElement }:
      {
        myNavigationService.Navigate(declaredElement, RiderMainWindowCenteredPopupWindowContextStub.Source, true);
        break; 
      }
      case NamedEntityDomainResolveResult invariantResolveResult:
      {
        var range = invariantResolveResult.NameDeclarationDocumentRange;
        range.Document.GetPsiSourceFile(mySolution).Navigate(new TextRange(range.StartOffset.Offset), true);
        break;
      }
      case DomainWebResourceResolveResult result:
      {
        try
        {
          var uri = new Uri(result.Link);
          if (!uri.IsHttpOrHttps()) break;
            
          myOpensUri.OpenUri(uri);
        }
        catch (Exception ex)
        {
          myLogger.Warn(ex);
        }
          
        break;
      }
    }
  }
}