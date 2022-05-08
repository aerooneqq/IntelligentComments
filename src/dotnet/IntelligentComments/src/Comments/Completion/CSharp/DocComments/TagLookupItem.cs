using System.Drawing;
using System.Text;
using JetBrains.Annotations;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems.Impl;
using JetBrains.UI.Icons;
using JetBrains.UI.RichText;

namespace IntelligentComments.Comments.Completion.CSharp.DocComments;

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
    var presentation = new RichText("<").Append(myTag, new TextStyle(JetFontStyles.Bold)).Append(Space);
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