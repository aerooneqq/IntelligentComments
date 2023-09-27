using System.Threading.Tasks;
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
using JetBrains.RdBackend.Common.Features.TextControls;
using JetBrains.ReSharper.Feature.Services.Protocol;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.DataContext;
using JetBrains.ReSharper.Psi.Tree;
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

    solution.GetProtocolSolution().GetRdCommentsModel().RequestClickDoc.SetAsync(HandleClickDocRequest);
  }

  [NotNull]
  private Task<int?> HandleClickDocRequest(Lifetime lifetime, RdCommentClickDocRequest request)
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

      const string Name = $"{nameof(ClickDocHost)}::DataRule";
      var dataRules = DataRules
        .AddRule(Name, ProjectModelDataConstants.SOLUTION, declaredElement.GetSolution())
        .AddRule(Name, PsiDataConstants.DECLARED_ELEMENTS, declaredElement.ToDeclaredElementsDataConstant())
        .AddRule(Name, DocumentModelDataConstants.DOCUMENT, textControl.Document)
        .AddRule(Name, PsiDataConstants.SOURCE_FILE, textControl.Document.GetPsiSourceFile(mySolution)!)
        .AddRule(Name, PsiDataConstants.SELECTED_TREE_NODES, EmptyList<ITreeNode>.Enumerable);

      var dataContext = myDataContexts.CreateWithDataRules(myLifetime, dataRules);
      
      using var _ = CompilationContextCookie.GetExplicitUniversalContextIfNotSet();
      var sessionId = myQuickDocHost.ExecuteSession(dataContext);
      task.Set(sessionId);
    });
    
    return task;
  }
}