using IntelligentComments.Comments.Caches.Names;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace IntelligentComments.Comments.Completion.CSharp.DocComments;

[Language(typeof(CSharpLanguage))]
public class CSharpReferenceSourceCompletionProvider : ItemsProviderOfSpecificContext<DocCommentCompletionContext>
{
  protected override bool AddLookupItems(DocCommentCompletionContext context, IItemsCollector collector)
  {
    if (context.TryGetContextAttribute() is not { } attribute ||
        DocCommentsBuilderUtil.TryExtractNameFromPossibleReferenceSourceAttribute(attribute) is not { } extraction ||
        CommentsCompletionExtensions.TryGetAttributeValueRanges(context.ContextToken) is not { } ranges)
    {
      return false;
    }

    var prefix = DocCommentsBuilderUtil.PreprocessText(attribute.UnquotedValue, null);
    var cache = NamesCacheUtil.GetCacheFor(context.GetSolution(), extraction.NameKind);
    foreach (var name in cache.GetAllNamesFor(prefix))
    {
      var lookupItem = new CommentLookupItem(name, name);
      lookupItem.InitializeRanges(ranges, context.BasicContext);
      collector.Add(lookupItem);
    }
    
    return true;
  }
}