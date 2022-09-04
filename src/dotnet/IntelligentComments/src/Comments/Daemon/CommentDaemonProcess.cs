using System;
using IntelligentComments.Comments.Calculations.Core;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.Util;

namespace IntelligentComments.Comments.Daemon;

public class CommentDaemonProcess : IDaemonStageProcess
{
  private readonly DaemonProcessKind myDaemonProcessKind;
  
  public IDaemonProcess DaemonProcess { get; }


  public CommentDaemonProcess([NotNull] IDaemonProcess process, DaemonProcessKind daemonProcessKind)
  {
    myDaemonProcessKind = daemonProcessKind;
    DaemonProcess = process;
  }
  
  
  public void Execute(Action<DaemonStageResult> committer)
  {
    var files = DaemonProcess.SourceFile.GetPsiFiles<KnownLanguage>();
    var result = new LocalList<HighlightingInfo>();
    
    foreach (var file in files)
    {
      if (LanguageManager.Instance.TryGetService<ICommentsProcessor>(file.Language) is not { } processor)
      {
        continue;
      }

      var context = CommentsProcessorContext.Create(myDaemonProcessKind);
      file.ProcessThisAndDescendants(processor, context);
      
      foreach (var (highlightingInfos, commentBase) in context.ProcessedComments)
      {
        if (highlightingInfos.Count == 0 && commentBase is { } comment && myDaemonProcessKind == DaemonProcessKind.VISIBLE_DOCUMENT)
        {
          result.Add(new HighlightingInfo(comment.Range, CommentFoldingHighlighting.Create(comment)));
        }

        if (highlightingInfos.Count > 0)
        {
          result.AddRange(highlightingInfos);
        }
      }
    }

    committer(new DaemonStageResult(result.ResultingList().AsIReadOnlyList()));
  }
}