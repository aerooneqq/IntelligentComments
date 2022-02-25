using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems.Impl;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpInvariantNameCompletionProvider : ItemsProviderOfSpecificContext<IntelligentCommentCompletionContext>
{
  protected override bool AddLookupItems(IntelligentCommentCompletionContext context, IItemsCollector collector)
  {
    if (!CommentsBuilderUtil.IsInvariantNameAttribute(context.TryGetContextAttribute())) return false;
    if (context.TryFindDocumentedEntity() is not IDeclaration declaration) return false;

    var name = declaration.DeclaredName + "::";
    var item = new TextLookupItem(name);
    item.InitializeRanges(context.TextLookupRanges, context.BasicContext);
    collector.Add(item);
    return true;
  }
}