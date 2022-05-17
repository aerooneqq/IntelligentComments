using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using IntelligentComments.Comments.Completion;
using IntelligentComments.Comments.Settings;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Tree;

namespace IntelligentComments.Comments.Languages.CSharp.Completion.DocComments;

[Language(typeof(CSharpLanguage))]
public class CSharpNamedEntityCompletionProvider : ItemsProviderOfSpecificContext<DocCommentCompletionContext>
{
  protected override bool AddLookupItems(DocCommentCompletionContext context, IItemsCollector collector)
  {
    if (!context.BasicContext.Solution.GetComponent<ICommentsSettings>().ExperimentalFeaturesEnabled.Value) return false;
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