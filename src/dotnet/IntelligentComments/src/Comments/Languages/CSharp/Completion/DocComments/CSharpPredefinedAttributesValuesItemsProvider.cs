using System.Collections.Generic;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Completion;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Xml.Tree;

namespace IntelligentComments.Comments.Languages.CSharp.Completion.DocComments;

[Language(typeof(CSharpLanguage))]
public class CSharpPredefinedAttributesValuesItemsProvider : ItemsProviderOfSpecificContext<DocCommentCompletionContext>
{
  [NotNull]
  private static readonly Dictionary<string, Dictionary<string, HashSet<string>>> ourAttributesValues =
    new()
    {
      [DocCommentsBuilderUtil.ListTagName] = new()
      {
        [DocCommentsBuilderUtil.ListTypeAttributeName] = new()
        {
          DocCommentsBuilderUtil.ListNumberType,
          DocCommentsBuilderUtil.ListBulletType, 
          DocCommentsBuilderUtil.ListTableType
        }
      }
    };

  protected override bool AddLookupItems(DocCommentCompletionContext context, IItemsCollector collector)
  {
    if (context.TryGetContextAttribute() is not { Parent: IXmlTagHeader { Name.XmlName: { } tagName } } attribute) return false;
    if (CommentsCompletionExtensions.TryGetAttributeValueRanges(context.ContextToken) is not { } ranges) return false;
    if (!ourAttributesValues.TryGetValue(tagName, out var attributesValues)) return false;
    if (!attributesValues.TryGetValue(attribute.AttributeName, out var values)) return false;

    foreach (var value in values)
    {
      var item = new CommentLookupItem(value, value);
      item.InitializeRanges(ranges, context.BasicContext);
      collector.Add(item);
    }

    return true;
  }
}