using IntelligentComments.Comments.Domain.Core.References;
using IntelligentComments.Comments.Domain.Impl.References;
using IntelligentComments.Rider.Comments.RdReferences;
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
using JetBrains.RdBackend.Common.Features.TextControls;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.DataContext;
using JetBrains.Rider.Backend.Features.QuickDoc;
using JetBrains.Rider.Model;
using JetBrains.Util;

namespace IntelligentComments.Rider.Comments.ClickDoc;

[SolutionComponent]
public class ClickDocHost
{
  private readonly Lifetime myLifetime;
  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly ISolution mySolution;
  [NotNull] private readonly QuickDocHost myQuickDocHost;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly ITextControlHost myTextControlHost;
  [NotNull] private readonly DataContexts myDataContexts;
  [NotNull] private readonly RdReferenceConverter myRdReferenceConverter;


  public ClickDocHost(
    Lifetime lifetime,
    [NotNull] ILogger logger,
    [NotNull] ISolution solution, 
    [NotNull] QuickDocHost quickDocHost,
    [NotNull] IShellLocks shellLocks,
    [NotNull] ITextControlHost textControlHost, 
    [NotNull] DataContexts dataContexts,
    [NotNull] RdReferenceConverter rdReferenceConverter)
  {
    myLifetime = lifetime;
    myLogger = logger;
    mySolution = solution;
    myQuickDocHost = quickDocHost;
    myShellLocks = shellLocks;
    myTextControlHost = textControlHost;
    myDataContexts = dataContexts;
    myRdReferenceConverter = rdReferenceConverter;

    solution.GetProtocolSolution().GetRdCommentsModel().RequestClickDoc.Set(HandleClickDocRequest);
  }
  
  
  [NotNull]
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
      var (_, textControlId) = request.ResolveRequest;
      if (myTextControlHost.TryGetTextControl(textControlId) is not { } textControl)
      {
        LogErrorAndSetNull($"Text control was null for {textControlId}");
        return;
      }

      if (myRdReferenceConverter.TryGetReference(request.ResolveRequest) is not { } reference)
      {
        LogErrorAndSetNull($"Declared element was null for {request.PrintToString()}");
        return;
      }
      
      var resolveContext = new DomainResolveContextImpl(mySolution, textControl.Document);
      if (reference.Resolve(resolveContext) is not DeclaredElementDomainResolveResult { DeclaredElement: { } declaredElement })
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