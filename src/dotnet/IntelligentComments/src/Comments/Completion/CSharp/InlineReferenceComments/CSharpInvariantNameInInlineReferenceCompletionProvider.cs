using IntelligentComments.Comments.Caches.Names;
using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace IntelligentComments.Comments.Completion.CSharp.InlineReferenceComments;

[Language(typeof(CSharpLanguage))]
public class CSharpInvariantNameInInlineReferenceCompletionProvider : ItemsProviderOfSpecificContext<InlineReferenceCommentCompletionContext>
{
  protected override bool AddLookupItems(InlineReferenceCommentCompletionContext context, IItemsCollector collector)
  {
    var prefix = context.Info.NameWithKind.Name;
    var cache = NamesCacheUtil.GetCacheFor(context.BasicContext.Solution, context.Info.NameWithKind.NameKind);

    foreach (var name in cache.GetAllNamesFor(prefix))
    {
      var lookupItem = new CommentLookupItem(name, name);
      lookupItem.InitializeRanges(context.Ranges, context.BasicContext);

      collector.Add(lookupItem);
    }

    return true;
  }
}