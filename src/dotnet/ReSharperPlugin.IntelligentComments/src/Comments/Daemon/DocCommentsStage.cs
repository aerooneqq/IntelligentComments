using System.Collections.Generic;
using System.Linq;
using JetBrains.Application.Settings;
using JetBrains.ReSharper.Daemon.CSharp.Stages;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.Util;

namespace ReSharperPlugin.IntelligentComments.Comments.Daemon;

[DaemonStage(StagesBefore = new[] { typeof(SmartResolverStage) })]
public class DocCommentsStage : IDaemonStage
{
  public IEnumerable<IDaemonStageProcess> CreateProcess(
    IDaemonProcess process, IContextBoundSettingsStore settings, DaemonProcessKind processKind)
  {
    if (processKind != DaemonProcessKind.VISIBLE_DOCUMENT) return EmptyList<IDaemonStageProcess>.Enumerable;
    return new[] { new DocCommentDaemonProcess(process) };
  }
}