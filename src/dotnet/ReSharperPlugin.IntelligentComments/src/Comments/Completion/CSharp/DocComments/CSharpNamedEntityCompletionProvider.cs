using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;

[Language(typeof(CSharpLanguage))]
public class CSharpNamedEntityCompletionProvider : ItemsProviderOfSpecificContext<DocCommentCompletionContext>
{
  protected override bool AddLookupItems(DocCommentCompletionContext context, IItemsCollector collector)
  {
    if (!DocCommentsBuilderUtil.IsNamedEntityAttribute(context.TryGetContextAttribute())) return false;
    if (context.TryFindDocumentedEntity() is not IDeclaration declaration) return false;
    if (CommentsCompletionExtensions.TryGetAttributeValueRanges(context.ContextToken) is not { } ranges) return false;

    var name = declaration.DeclaredName;
    var item = new CommentLookupItem(name, name);
    item.InitializeRanges(ranges, context.BasicContext);
    
    collector.Add(item);
    return true;
  }
}