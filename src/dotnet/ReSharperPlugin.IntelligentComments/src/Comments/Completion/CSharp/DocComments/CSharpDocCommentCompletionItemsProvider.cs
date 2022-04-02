using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.CompletionInDocComments;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;

[Language(typeof(CSharpLanguage))]
public class CSharpDocCommentCompletionItemsProvider : ItemsProviderOfSpecificContext<ContextInDocComment>
{
  private record TagInfo(string Tag, string[] Attributes, bool ClosedTag)
  {
    [NotNull] public TagLookupItem ToLookupItem() => new(Tag, Attributes, ClosedTag);
  }

  [ItemNotNull] [NotNull] private static readonly TagInfo[] ourClosedTags =
  {
    new("image", new[] { CommentsBuilderUtil.ImageSourceAttrName }, true),
    new("invariant", new[] { CommentsBuilderUtil.InvariantNameAttrName }, false),
    new("reference", new[] { CommentsBuilderUtil.InvariantReferenceSourceAttrName }, false)
  };
  
  protected override bool AddLookupItems(ContextInDocComment context, IItemsCollector collector)
  {
    var lookupItems = ourClosedTags.Select(info =>
    {
      var lookupItem = info.ToLookupItem();
      lookupItem.InitializeRanges(context.TextLookupRanges, context.BasicContext);
      return lookupItem;
    });
    
    foreach (var item in lookupItems)
    {
      collector.Add(item);
    }
    
    return true;
  }
}