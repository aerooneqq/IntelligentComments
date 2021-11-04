using System;
using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.DocumentModel;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

[SolutionComponent]
public class CommentsCalculationsManager
{
  private readonly Lifetime myLifetime;
  [NotNull] private readonly IDictionary<string, SequentialLifetimes> myLifetimes;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly ISolution mySolution;


  public CommentsCalculationsManager(
    Lifetime lifetime,
    [NotNull] ISolution solution,
    [NotNull] IShellLocks shellLocks)
  {
    myLifetime = lifetime;
    mySolution = solution;
    myShellLocks = shellLocks;
    myLifetimes = new Dictionary<string, SequentialLifetimes>();
  }


  public void CalculateFor(
    [NotNull] IDocument document,
    [NotNull] Action<IEnumerable<ICommentBase>> afterCalculationAction)
  {
    myShellLocks.AssertMainThread();

    var lifetimes = myLifetimes.GetOrCreateValue(document.Moniker, () => new SequentialLifetimes(myLifetime));
    var lifetime = lifetimes.Next();

    IEnumerable<ICommentBase> comments = null;
    var ira = new InterruptableReadActivityThe(lifetime, myShellLocks, () => lifetime.IsNotAlive)
    {
      FuncRun = () =>
      {
        var calculator = new CommentsCalculatorImpl(mySolution, myShellLocks);
        comments = calculator.CalculateFor(document);
      },
      FuncCompleted = () => afterCalculationAction(comments)
    };

    ira.DoStart();
  }
}