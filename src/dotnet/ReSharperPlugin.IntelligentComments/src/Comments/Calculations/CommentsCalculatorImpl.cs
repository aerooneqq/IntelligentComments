using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.Util;
using JetBrains.Util.Logging;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public class CommentsCalculatorImpl : ICommentsCalculator
{
  private static readonly ILogger ourLogger = Logger.GetLogger<CommentsCalculatorImpl>();
  
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly ISolution mySolution;


  public CommentsCalculatorImpl([NotNull] ISolution solution, [NotNull] IShellLocks shellLocks)
  {
    mySolution = solution;
    myShellLocks = shellLocks;
  }


  public IEnumerable<ICommentBase> CalculateFor(IDocument document)
  {
    myShellLocks.AssertReadAccessAllowed();

    if (document.GetPsiSourceFile(mySolution)?.GetPrimaryPsiFile() is not ICSharpFile file)
      return EmptyList<IIntelligentComment>.Enumerable;

    var processor = new XmlDocsProcessor();
    file.ProcessThisAndDescendants(processor);
    return processor.Comments;
  }
}