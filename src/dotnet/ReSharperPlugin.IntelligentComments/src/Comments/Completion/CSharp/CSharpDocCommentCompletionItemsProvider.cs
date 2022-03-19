using System.Drawing;
using System.Linq;
using System.Text;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.CompletionInDocComments;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems.Impl;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.UI.Icons;
using JetBrains.UI.RichText;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;

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

internal class TagLookupItem : TextLookupItemBase
{
  private const char Space = ' ';
  
  [NotNull] private readonly string myTag;
  [ItemNotNull] [NotNull] private readonly string[] myAttributes;
  private readonly bool myClosedTag;


  public sealed override string Text { get; set; }
  public override IconId Image => null;


  public TagLookupItem([NotNull] string tag, [ItemNotNull] [NotNull] string[] attributes, bool closedTag)
  {
    myTag = tag;
    myAttributes = attributes;
    myClosedTag = closedTag;

    // ReSharper disable once VirtualMemberCallInConstructor
    Text = CreateTextAndInitOffsets();
  }


  private string CreateTextAndInitOffsets()
  {
    var sb = new StringBuilder();
    sb.Append("<").Append(myTag).Append(Space);

    foreach (var attribute in myAttributes)
    {
      sb.Append(attribute).Append(Space).Append("=").Append(Space).Append("\"\"");
    }

    if (myAttributes.Length == 0)
    {
      sb.Remove(sb.Length - 1, 1);
    }

    int CalculateFirstAttrValueOffset() => 1 + myTag.Length + 1 + myAttributes[0].Length + 1 + 1 + 1 + 1;
    int CalculateFirstAttrValueOffsetFromEnd() => sb.Length - CalculateFirstAttrValueOffset();
    
    int offset;
    if (myClosedTag)
    {
      sb.Append(">").Append("</").Append(myTag).Append(">");
      offset = myAttributes.Length switch
      {
        > 0 => CalculateFirstAttrValueOffsetFromEnd(),
        _ => 2 + myTag.Length + 1
      };
    }
    else
    {
      sb.Append(Space).Append("/>");
      offset = myAttributes.Length switch
      {
        > 0 => CalculateFirstAttrValueOffsetFromEnd(),
        _ => 0
      };
    }
    
    InsertCaretOffset = -offset;
    ReplaceCaretOffset = -offset;

    return sb.ToString();
  }

  protected override RichText GetDisplayName()
  {
    var presentation = new RichText("<").Append(myTag, new TextStyle(FontStyle.Bold)).Append(Space);
    foreach (var attribute in myAttributes)
    {
      presentation = presentation.Append(attribute).Append(Space).Append("=").Append(Space).Append("\"\"").Append(Space);
    }

    if (myAttributes.Length == 0)
    {
      presentation = presentation.Remove(presentation.Length - 1, 1);
    }

    if (myClosedTag)
    {
      if (myAttributes.Length != 0)
      {
        presentation = presentation.Remove(presentation.Length - 1, 1);
      }

      presentation = presentation.Append(">").Append("</").Append(myTag).Append(">");
    }
    else
    {
      presentation = presentation.Append("/>");
    }

    return presentation;
  }
}