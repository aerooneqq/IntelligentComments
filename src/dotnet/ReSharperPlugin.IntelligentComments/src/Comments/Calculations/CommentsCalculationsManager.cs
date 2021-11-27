using System;
using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.DataFlow;
using JetBrains.DocumentModel;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features.ProjectModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

[SolutionComponent]
public class CommentsCalculationsManager
{
  private readonly Lifetime myLifetime;
  [NotNull] private readonly IDictionary<string, SequentialLifetimes> myLifetimes;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly IPsiServices myPsiServices;
  [NotNull] private readonly RiderSolutionLoadStateMonitor mySolutionLoadStateMonitor;
  [NotNull] private readonly ICommentsCalculator myCommentsCalculator;


  public CommentsCalculationsManager(
    Lifetime lifetime,
    [NotNull] IShellLocks shellLocks,
    [NotNull] IPsiServices psiServices,
    [NotNull] RiderSolutionLoadStateMonitor solutionLoadStateMonitor,
    [NotNull] ICommentsCalculator commentsCalculator)
  {
    myLifetime = lifetime;
    myShellLocks = shellLocks;
    myPsiServices = psiServices;
    mySolutionLoadStateMonitor = solutionLoadStateMonitor;
    myCommentsCalculator = commentsCalculator;
    myLifetimes = new Dictionary<string, SequentialLifetimes>();
  }


  public void CalculateFor(
    [NotNull] IDocument document,
    [NotNull] Action<IEnumerable<ICommentBase>> afterCalculationAction)
  {
    mySolutionLoadStateMonitor.SolutionLoadedAndProjectModelCachesReady.WhenTrueOnce(myLifetime, () =>
    {
      myPsiServices.CachesState.IsIdle.WhenTrueOnce(myLifetime, () =>
      {
        myShellLocks.ExecuteOrQueueReadLock($"{nameof(CalculateFor)}", () =>
        {
          myPsiServices.Files.ExecuteAfterCommitAllDocuments(() =>
          {
            myShellLocks.AssertMainThread();
            var lifetimes = myLifetimes.GetOrCreateValue(document.Moniker, () => new SequentialLifetimes(myLifetime));
            var lifetime = lifetimes.Next();

            IEnumerable<ICommentBase> comments = null;
            var ira = new InterruptableReadActivityThe(lifetime, myShellLocks, () => lifetime.IsNotAlive)
            {
              FuncRun = () => comments = myCommentsCalculator.CalculateFor(document),
              FuncCompleted = () => afterCalculationAction(comments)
            };

            ira.DoStart();
          });
        });
      });
    });
  }
}