using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems.Impl;
using JetBrains.UI.Icons;
using JetBrains.UI.RichText;
using System.Drawing;
using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;

internal class CommentLookupItem : TextLookupItemBase
{
  [NotNull] private readonly string myPresentation;
  
  
  public sealed override string Text { get; set; }
  public override IconId Image => null;


  public CommentLookupItem(string insertionText, string presentation, int caretOffset = 0)
  {
    myPresentation = presentation;
    Text = insertionText;
    InsertCaretOffset = caretOffset;
    ReplaceCaretOffset = caretOffset;
  }
  

  protected override RichText GetDisplayName()
  {
    return new RichText(myPresentation, new TextStyle(FontStyle.Bold));
  }
}