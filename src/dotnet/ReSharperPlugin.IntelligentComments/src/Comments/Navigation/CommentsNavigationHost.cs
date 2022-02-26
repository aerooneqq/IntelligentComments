using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.Core;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.Rd.Tasks;
using JetBrains.RdBackend.Common.Features;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.RdBackend.Common.Features.Services;
using JetBrains.ReSharper.Feature.Services.Navigation;
using JetBrains.ReSharper.Psi;
using JetBrains.Rider.Backend.Features.Documents;
using JetBrains.Rider.Model;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation;

[SolutionComponent]
public class CommentsNavigationHost
{
  private readonly Lifetime myLifetime;
  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly ISolution mySolution;
  [NotNull] private readonly IDeclaredElementNavigationService myNavigationService;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly RdReferenceConverter myRdReferenceConverter;
  [NotNull] private readonly DocumentHostBase myDocumentHostBase;


  public CommentsNavigationHost(
    Lifetime lifetime,
    [NotNull] ILogger logger,
    [NotNull] ISolution solution,
    [NotNull] IDeclaredElementNavigationService navigationService,
    [NotNull] IShellLocks shellLocks,
    [NotNull] RdReferenceConverter rdReferenceConverter,
    [NotNull] RiderDocumentHost documentHost)
  {
    myLifetime = lifetime;
    myLogger = logger;
    mySolution = solution;
    myNavigationService = navigationService;
    myShellLocks = shellLocks;
    myRdReferenceConverter = rdReferenceConverter;
    myDocumentHostBase = documentHost;
    
    solution.GetProtocolSolution().GetRdCommentsModel().PerformNavigation.Set(HandleNavigationRequest);
  }

  
  private RdTask<Unit> HandleNavigationRequest(Lifetime lifetime, RdNavigationRequest request)
  {
    var task = new RdTask<Unit>();
    void LogWarnAndSetNull(string message)
    {
      myLogger.Warn(message);
      task.Set(Unit.Instance);
    }
    
    myShellLocks.QueueReadLock(myLifetime, $"{nameof(CommentsNavigationHost)}::ServingRequest", () =>
    {
      using var _ = CompilationContextCookie.GetExplicitUniversalContextIfNotSet();
      var (rdReference, textControlId) = request.ResolveRequest;
      if (myRdReferenceConverter.TryGetReference(rdReference, textControlId) is not { } reference)
      {
        LogWarnAndSetNull($"Failed to get reference for {rdReference}");
        return;
      }

      if (myDocumentHostBase.TryGetHostDocument(textControlId.DocumentId) is not { } document)
      {
        LogWarnAndSetNull($"Failed to get document for {textControlId}");
        return;
      }

      var resolveContext = new ResolveContextImpl(mySolution, document);
      var resolveResult = reference.Resolve(resolveContext);
      if (resolveResult is not DeclaredElementResolveResult { DeclaredElement: { } declaredElement })
      {
        LogWarnAndSetNull("Need declared element in order to perform navigation");
        return;
      }
      
      myNavigationService.Navigate(declaredElement, RiderMainWindowCenteredPopupWindowContextStub.Source, true);
      task.Set(Unit.Instance);
    });

    return task;
  }
}