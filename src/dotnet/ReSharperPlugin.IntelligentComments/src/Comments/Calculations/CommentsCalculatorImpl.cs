using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Application.BuildScript.Application.Zones;
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

[SolutionComponent]
public class CommentsCalculatorImpl : ICommentsCalculator
{
  private static readonly ILogger ourLogger = Logger.GetLogger<CommentsCalculatorImpl>();

  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly ISolution mySolution;
  [NotNull] private readonly IHighlightersProvider myHighlightersProvider;


  public CommentsCalculatorImpl(
    [NotNull] ISolution solution, [NotNull] IShellLocks shellLocks, IHighlightersProvider highlightersProvider)
  {
    myHighlightersProvider = highlightersProvider;
    mySolution = solution;
    myShellLocks = shellLocks;
  }


  public IEnumerable<ICommentBase> CalculateFor(IDocument document)
  {
    myShellLocks.AssertReadAccessAllowed();
    mySolution.GetPsiServices().Files.AssertAllDocumentAreCommitted();
      
    var file = document.GetPsiSourceFile(mySolution)?.GetPrimaryPsiFile();
    if (file is not ICSharpFile)
    {
      ourLogger.LogAssertion($"Primary psi file is not C# ({file?.GetType().Name}) one {document.Moniker}");
      return EmptyList<IIntelligentComment>.Enumerable;
    }
      
    var processor = new XmlDocsProcessor(myHighlightersProvider);
    file.ProcessThisAndDescendants(processor);
    return processor.Comments;
  }
}