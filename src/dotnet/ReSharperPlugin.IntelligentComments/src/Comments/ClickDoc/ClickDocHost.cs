using JetBrains.Annotations;
using JetBrains.Application.DataContext;
using JetBrains.Application.Threading;
using JetBrains.DocumentModel.DataContext;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.ProjectModel.DataContext;
using JetBrains.Rd.Base;
using JetBrains.Rd.Tasks;
using JetBrains.RdBackend.Common.Features;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.DataContext;
using JetBrains.Rider.Backend.Features.QuickDoc;
using JetBrains.Rider.Backend.Features.TextControls;
using JetBrains.Rider.Model;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.ClickDoc;

[SolutionComponent]
public class ClickDocHost
{
  private readonly Lifetime myLifetime;
  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly ISolution mySolution;
  [NotNull] private readonly QuickDocHost myQuickDocHost;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly RiderTextControlHost myTextControlHost;
  [NotNull] private readonly DataContexts myDataContexts;
  [NotNull] private readonly ReferencesCache myReferencesCache;
  [NotNull] private readonly RdReferenceConverter myRdReferenceConverter;


  public ClickDocHost(
    Lifetime lifetime,
    [NotNull] ILogger logger,
    [NotNull] ISolution solution, 
    [NotNull] QuickDocHost quickDocHost,
    [NotNull] IShellLocks shellLocks,
    [NotNull] RiderTextControlHost textControlHost, 
    [NotNull] DataContexts dataContexts, 
    [NotNull] ReferencesCache referencesCache,
    [NotNull] RdReferenceConverter rdReferenceConverter)
  {
    myLifetime = lifetime;
    myLogger = logger;
    mySolution = solution;
    myQuickDocHost = quickDocHost;
    myShellLocks = shellLocks;
    myTextControlHost = textControlHost;
    myDataContexts = dataContexts;
    myReferencesCache = referencesCache;
    myRdReferenceConverter = rdReferenceConverter;

    solution.GetProtocolSolution().GetRdCommentsModel().RequestClickDoc.Set(HandleClickDocRequest);
  }
  
  
  private RdTask<int?> HandleClickDocRequest(Lifetime lifetime, RdCommentClickDocRequest request)
  {
    var task = new RdTask<int?>();
    void LogErrorAndSetNull([NotNull] string error)
    {
      myLogger.Error(error);
      task.Set((int?)null);
    }
    
    myShellLocks.QueueReadLock(myLifetime, $"{nameof(ClickDocHost)}::ServingRequest", () =>
    {
      if (myTextControlHost.TryGetTextControl(request.TextControlId) is not { } textControl)
      {
        LogErrorAndSetNull($"Text control was null for {request.TextControlId}");
        return;
      }

      if (myRdReferenceConverter.TryGetReference(request.Reference, request.TextControlId) is not { } reference)
      {
        LogErrorAndSetNull($"Declared element was null for {request.PrintToString()}");
        return;
      }

      if (reference is IProxyReference proxyReference)
      {
        reference = myReferencesCache[textControl.Document, proxyReference.RealReferenceId]?.Reference;
      }

      var resolveContext = new ResolveContextImpl(mySolution, textControl.Document);
      if (reference?.Resolve(resolveContext) is not DeclaredElementResolveResult { DeclaredElement: { } declaredElement })
      {
        LogErrorAndSetNull($"Declared element was null for {request.PrintToString()}");
        return;
      }

      const string name = $"{nameof(ClickDocHost)}::DataRule";
      var dataRules = DataRules
        .AddRule(name, ProjectModelDataConstants.SOLUTION, declaredElement.GetSolution())
        .AddRule(name, PsiDataConstants.DECLARED_ELEMENTS, declaredElement.ToDeclaredElementsDataConstant())
        .AddRule(name, DocumentModelDataConstants.DOCUMENT, textControl.Document);

      var dataContext = myDataContexts.CreateWithDataRules(myLifetime, dataRules);
      
      using var _ = CompilationContextCookie.GetExplicitUniversalContextIfNotSet();
      var sessionId = myQuickDocHost.ExecuteSession(dataContext);
      task.Set(sessionId);
    });
    
    return task;
  }
}