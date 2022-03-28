using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpReferenceSourceCompletionProvider : ItemsProviderOfSpecificContext<IntelligentCommentCompletionContext>
{
  protected override bool AddLookupItems(IntelligentCommentCompletionContext context, IItemsCollector collector)
  {
    var attribute = context.TryGetContextAttribute();
    if (attribute is null || !CommentsBuilderUtil.IsInvariantReferenceSourceAttribute(attribute)) return false;

    var prefix = CommentsBuilderUtil.PreprocessText(attribute.UnquotedValue, null);
    var cache = context.GetSolution().GetComponent<InvariantsNamesCache>();
    foreach (var name in cache.GetAllNamesFor(prefix))
    {
      var lookupItem = new CommentLookupItem(name);
      lookupItem.InitializeRanges(context.TextLookupRanges, context.BasicContext);

      collector.Add(lookupItem);
    }
    
    return true;
  }
}