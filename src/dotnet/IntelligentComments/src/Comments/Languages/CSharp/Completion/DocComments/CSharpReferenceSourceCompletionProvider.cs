using IntelligentComments.Comments.Caches.Names;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Completion;
using IntelligentComments.Comments.Settings;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace IntelligentComments.Comments.Languages.CSharp.Completion.DocComments;

[Language(typeof(CSharpLanguage))]
public class CSharpReferenceSourceCompletionProvider : ItemsProviderOfSpecificContext<DocCommentCompletionContext>
{
  protected override bool AddLookupItems(DocCommentCompletionContext context, IItemsCollector collector)
  {
    if (context.TryGetContextAttribute() is not { } attribute ||
        DocCommentsBuilderUtil.TryExtractNameFromPossibleReferenceSourceAttribute(attribute) is not { } extraction ||
        CommentsCompletionExtensions.TryGetAttributeValueRanges(context.ContextToken) is not { } ranges ||
        !context.BasicContext.Solution.GetComponent<ICommentsSettings>().ExperimentalFeaturesEnabled.Value)
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