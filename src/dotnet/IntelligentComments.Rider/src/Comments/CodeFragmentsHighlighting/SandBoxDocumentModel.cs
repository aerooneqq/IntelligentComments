using JetBrains.Collections.Viewable;
using JetBrains.Core;
using JetBrains.Rd.Tasks;
using JetBrains.Rd.Text.Impl;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.Rider.Model;

namespace IntelligentComments.Rider.Comments.CodeFragmentsHighlighting;

internal class SandBoxDocumentModel : IDocumentViewModel
{
  public AbstractSandboxInfo SandboxInfo { get; }
  public IViewableProperty<CrumbSession> CrumbsSession { get; }
  public IViewableProperty<RdMarkupModelBase> Markup { get; }
  public IViewableMap<TextControlId, TextControlModel> TextControls { get; }
  public RdTextBuffer Text { get; }
  public IRdEndpoint<Unit, string> CompareAllTextTask { get; }


  public SandBoxDocumentModel(SandboxInfo sandboxInfo)
  {
    SandboxInfo = sandboxInfo;
    CrumbsSession = new ViewableProperty<CrumbSession>();
    Markup = new ViewableProperty<RdMarkupModelBase>();
    TextControls = new ViewableMap<TextControlId, TextControlModel>();
    Text = new RdTextBuffer();
    CompareAllTextTask = new RdCall<Unit, string>();
  }
}