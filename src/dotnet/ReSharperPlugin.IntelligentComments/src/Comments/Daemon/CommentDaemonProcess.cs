using System;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
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
    var files = DaemonProcess.SourceFile.GetPsiFiles<KnownLanguage>();
    var result = new LocalList<HighlightingInfo>();
    
    foreach (var file in files)
    {
      var collector = LanguageManager.Instance.GetService<ICommentsProcessor>(file.Language);
      file.ProcessThisAndDescendants(collector);
      foreach (var commentProcessingResult in collector.ProcessedComments)
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