using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.CompletionInDocComments;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;

[Language(typeof(CSharpLanguage))]
public class CSharpDocCommentCompletionItemsProvider : ItemsProviderOfSpecificContext<ContextInDocComment>
{
  private record TagInfo(string Tag, string[] Attributes, bool ClosedTag)
  {
    [NotNull] public TagLookupItem ToLookupItem() => new(Tag, Attributes, ClosedTag);
  }

  [ItemNotNull] [NotNull] 
  private static readonly TagInfo[] ourTags =
  {
    new(DocCommentsBuilderUtil.ImageTagName, new[] { DocCommentsBuilderUtil.ImageSourceAttrName }, true),
    new(DocCommentsBuilderUtil.InvariantTagName, new[] { DocCommentsBuilderUtil.InvariantNameAttrName }, false),
    new(DocCommentsBuilderUtil.ReferenceTagName, new[] { DocCommentsBuilderUtil.InvariantReferenceSourceAttrName }, false),
    new(DocCommentsBuilderUtil.ReferenceTagName, new[] { DocCommentsBuilderUtil.HackReferenceSourceAttributeName }, false),
    new(DocCommentsBuilderUtil.ReferenceTagName, new[] { DocCommentsBuilderUtil.TodoReferenceSourceAttributeName }, false),
    new(DocCommentsBuilderUtil.TodoTagName, EmptyArray.GetInstance<string>(), true),
    new(DocCommentsBuilderUtil.TicketsSectionTagName, EmptyArray.GetInstance<string>(), true),
    new(DocCommentsBuilderUtil.TicketTagName, DocCommentsBuilderUtil.PossibleTicketAttributes.ToArray(), true),
    new(DocCommentsBuilderUtil.DescriptionTagName, EmptyArray.GetInstance<string>(), true),
    new(DocCommentsBuilderUtil.HackTagName, EmptyArray.GetInstance<string>(), true)
  };
  
  protected override bool AddLookupItems(ContextInDocComment context, IItemsCollector collector)
  {
    var lookupItems = ourTags.Select(info =>
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