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

public class CommentDaemonProcess : IDaemonStageProcess
{
  public IDaemonProcess DaemonProcess { get; }


  public CommentDaemonProcess([NotNull] IDaemonProcess process)
  {
    DaemonProcess = process;
  }
  
  
  public void Execute(Action<DaemonStageResult> committer)
  {
    IEnumerable<IFile> files = DaemonProcess.SourceFile.GetPsiFiles<CSharpLanguage>();
    var result = new List<HighlightingInfo>();
    foreach (IFile file in files)
    {
      var commentsCollector = new CommentsProcessor();
      file.ProcessThisAndDescendants(commentsCollector);
      foreach (ICommentBase comment in commentsCollector.Comments)
      {
        result.Add(new HighlightingInfo(comment.Range, CommentFoldingHighlighting.Create(comment)));
      }
    }

    committer(new DaemonStageResult(result));
  }
}