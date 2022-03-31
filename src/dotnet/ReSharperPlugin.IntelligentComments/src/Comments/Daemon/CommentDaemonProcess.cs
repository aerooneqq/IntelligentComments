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
      var processor = CommentsProcessorsProvider.CreateProcessorFor(file.Language, myDaemonProcessKind);
      file.ProcessThisAndDescendants(processor);
      foreach (var (highlightingInfos, commentBase) in processor.ProcessedComments)
      {
        if (commentBase is { } comment && myDaemonProcessKind == DaemonProcessKind.VISIBLE_DOCUMENT)
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