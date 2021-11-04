using System.Linq;
using JetBrains.Annotations;
using JetBrains.Collections.Viewable;
using JetBrains.DataFlow;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features;
using JetBrains.Rider.Backend.Features.TextControls;
using JetBrains.Rider.Model;
using JetBrains.TextControl;
using ReSharperPlugin.IntelligentComments.Comments;
using ReSharperPlugin.IntelligentComments.Comments.Calculations;
using ReSharperPlugin.IntelligentComments.Comments.Domain;

namespace ReSharperPlugin.IntelligentComments.Core;

[SolutionComponent]
public class CommentsEditorsHost
{
  [NotNull] private readonly CommentsCalculationsManager myCommentsCalculationsManager;
  [NotNull] private readonly RdCommentsModel myRdCommentsHost;
  [NotNull] private readonly RiderTextControlHost myTextControlHost;


  public CommentsEditorsHost(
    Lifetime lifetime,
    [NotNull] ISolution solution,
    [NotNull] ITextControlManager textControlManager,
    [NotNull] CommentsCalculationsManager commentsCalculationsManager,
    [NotNull] RiderTextControlHost textControlHost)
  {
    myCommentsCalculationsManager = commentsCalculationsManager;
    myTextControlHost = textControlHost;
    myRdCommentsHost = solution.GetProtocolSolution().GetRdCommentsModel();
    textControlManager.VisibleTextControls.AddRemove.Advise(lifetime, HandleVisibleEditorsChange);
  }


  private void HandleVisibleEditorsChange(AddRemoveEventArgs<ITextControl> args)
  {
    if (args.Action == AddRemove.Add)
    {
      var document = args.Value.Document;
      myCommentsCalculationsManager.CalculateFor(document, comments =>
      {
        if (comments is null) return;
        var rdComments = new RdDocumentComments(comments.Select(CommentsUtil.ToRdComment).ToList());

        var (documentId, _, _, _) = myTextControlHost.GetTextControlId(args.Value);
        myRdCommentsHost.Comments[documentId] = rdComments;
      });
    }
  }
}