using System;
using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Daemon.CSharp.CodeFoldings;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using JetBrains.Util.Logging;
using ReSharperPlugin.IntelligentComments.Comments.Calculations;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Daemon;

public class DocCommentDaemonProcess : IDaemonStageProcess
{
  [NotNull] private readonly IHighlightersProvider myProvider;
  
  
  public IDaemonProcess DaemonProcess { get; }


  public DocCommentDaemonProcess([NotNull] IDaemonProcess process)
  {
    myProvider = process.Solution.GetComponent<IHighlightersProvider>();
    DaemonProcess = process;
  }
  
  
  public void Execute(Action<DaemonStageResult> committer)
  {
    var files = DaemonProcess.SourceFile.GetPsiFiles<CSharpLanguage>();
    var result = new List<HighlightingInfo>();
    foreach (var file in files)
    {
      var commentsCollector = new XmlDocsProcessor(myProvider);
      file.ProcessThisAndDescendants(commentsCollector);
      foreach (var comment in commentsCollector.Comments.SafeOfType<IDocComment>())
      {
        var range = comment.CommentOwnerPointer.GetTreeNode().GetDocumentRange();
        result.Add(new HighlightingInfo(range, DocCommentFoldingHighlighting.Create(comment)));
      }
    }

    committer(new DaemonStageResult(result));
  }
}