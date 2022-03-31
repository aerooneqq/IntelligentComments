using System.Collections.Generic;
using JetBrains.Application.Settings;
using JetBrains.ReSharper.Daemon.CSharp.Stages;
using JetBrains.ReSharper.Feature.Services.Daemon;

namespace ReSharperPlugin.IntelligentComments.Comments.Daemon;

[DaemonStage(StagesBefore = new[] { typeof(SmartResolverStage) })]
public class CommentsCollectionStage : IDaemonStage
{
  public IEnumerable<IDaemonStageProcess> CreateProcess(
    IDaemonProcess process, IContextBoundSettingsStore settings, DaemonProcessKind processKind)
  {
    return new[] { new CommentDaemonProcess(process, processKind) };
  }
}