using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems.Impl;
using JetBrains.UI.Icons;
using JetBrains.UI.RichText;
using System.Drawing;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;

internal class CommentLookupItem : TextLookupItemBase
{
  public sealed override string Text { get; set; }
  public override IconId Image => null;


  public CommentLookupItem(string name)
  {
    Text = name;
  }
    

  protected override RichText GetDisplayName()
  {
    return new RichText(Text, new TextStyle(FontStyle.Bold));
  }
}