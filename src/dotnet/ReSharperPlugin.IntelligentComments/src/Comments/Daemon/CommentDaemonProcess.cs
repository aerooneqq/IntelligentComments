using System;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations;

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
    var files = DaemonProcess.SourceFile.GetPsiFiles<CSharpLanguage>();
    var result = new LocalList<HighlightingInfo>();
    
    foreach (var file in files)
    {
      var commentsCollector = new CommentsProcessor();
      file.ProcessThisAndDescendants(commentsCollector);
      foreach (var commentProcessingResult in commentsCollector.Comments)
      {
        if (commentProcessingResult.CommentBase is { } comment)
        {
          result.Add(new HighlightingInfo(comment.Range, CommentFoldingHighlighting.Create(comment)));
        }

        if (commentProcessingResult.Errors.Count > 0)
        {
          result.AddRange(commentProcessingResult.Errors);
        }
      }
    }

    committer(new DaemonStageResult(result.ResultingList().AsIReadOnlyList()));
  }
}