using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems.Impl;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpReferenceCompletionProvider : ItemsProviderOfSpecificContext<IntelligentCommentCompletionContext>
{
  protected override bool AddLookupItems(IntelligentCommentCompletionContext context, IItemsCollector collector)
  {
    var attribute = context.TryGetContextAttribute();
    if (!CommentsBuilderUtil.IsReferenceSourceAttribute(attribute)) return false;

    var cache = context.GetSolution().GetComponent<InvariantsNamesCache>();
    foreach (var name in cache.Map[context.BasicContext.SourceFile])
    {
      var lookupItem = new TextLookupItem(name.Key);
      lookupItem.InitializeRanges(context.TextLookupRanges, context.BasicContext);

      collector.Add(lookupItem);
    }
    
    return true;
  }
}