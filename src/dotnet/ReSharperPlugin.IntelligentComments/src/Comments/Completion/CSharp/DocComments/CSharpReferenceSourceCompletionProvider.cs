using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;

[Language(typeof(CSharpLanguage))]
public class CSharpReferenceSourceCompletionProvider : ItemsProviderOfSpecificContext<DocCommentCompletionContext>
{
  protected override bool AddLookupItems(DocCommentCompletionContext context, IItemsCollector collector)
  {
    if (context.TryGetContextAttribute() is not { } attribute ||
        DocCommentsBuilderUtil.TryExtractNameFromPossibleReferenceSourceAttribute(attribute) is not { } extraction)
    {
      return false;
    }

    var prefix = DocCommentsBuilderUtil.PreprocessText(attribute.UnquotedValue, null);
    var cache = NamesCacheUtil.GetCacheFor(context.GetSolution(), extraction.NameKind);
    foreach (var name in cache.GetAllNamesFor(prefix))
    {
      var lookupItem = new CommentLookupItem(name);
      lookupItem.InitializeRanges(context.TextLookupRanges, context.BasicContext);

      collector.Add(lookupItem);
    }
    
    return true;
  }
}