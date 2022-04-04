using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;

[Language(typeof(CSharpLanguage))]
public class CSharpReferenceSourceCompletionProvider : ItemsProviderOfSpecificContext<DocCommentCompletionContext>
{
  protected override bool AddLookupItems(DocCommentCompletionContext context, IItemsCollector collector)
  {
    var attribute = context.TryGetContextAttribute();
    if (attribute is null || !DocCommentsBuilderUtil.IsInvariantReferenceSourceAttribute(attribute)) return false;

    var prefix = DocCommentsBuilderUtil.PreprocessText(attribute.UnquotedValue, null);
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