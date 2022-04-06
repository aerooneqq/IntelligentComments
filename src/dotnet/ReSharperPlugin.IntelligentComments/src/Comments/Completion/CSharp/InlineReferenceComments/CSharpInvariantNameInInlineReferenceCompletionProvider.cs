using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names.Invariants;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.InlineReferenceComments;

[Language(typeof(CSharpLanguage))]
public class CSharpInvariantNameInInlineReferenceCompletionProvider : ItemsProviderOfSpecificContext<InlineReferenceCommentCompletionContext>
{
  protected override bool AddLookupItems(InlineReferenceCommentCompletionContext context, IItemsCollector collector)
  {
    var prefix = context.Info.InvariantName;
    var cache = context.BasicContext.Solution.GetComponent<InvariantsNamesNamesCache>();

    foreach (var name in cache.GetAllNamesFor(prefix))
    {
      var lookupItem = new CommentLookupItem(name);
      lookupItem.InitializeRanges(context.Ranges, context.BasicContext);

      collector.Add(lookupItem);
    }

    return true;
  }
}