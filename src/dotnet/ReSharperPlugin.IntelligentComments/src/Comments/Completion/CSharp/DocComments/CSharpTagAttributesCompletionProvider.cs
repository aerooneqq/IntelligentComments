using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure.LookupItems;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Parsing;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;

[Language(typeof(CSharpLanguage))]
public class CSharpTagAttributesCompletionProvider : ItemsProviderOfSpecificContext<DocCommentCompletionContext>
{
  [NotNull] private static readonly IReadOnlyDictionary<string, ISet<string>> ourTagsPossibleAttributes =
    new Dictionary<string, ISet<string>>
    {
      [DocCommentsBuilderUtil.ListTagName] = new HashSet<string> { DocCommentsBuilderUtil.ListTypeAttributeName },
      
      [DocCommentsBuilderUtil.ParamTagName] = new HashSet<string> { DocCommentsBuilderUtil.ParamNameAttrName },
      [DocCommentsBuilderUtil.ParamRefTagName] = new HashSet<string> { DocCommentsBuilderUtil.ParamRefNameAttrName },
      [DocCommentsBuilderUtil.TypeParamTagName] = new HashSet<string> { DocCommentsBuilderUtil.TypeParamNameAttrName },
      [DocCommentsBuilderUtil.TypeParamRefTagName] = new HashSet<string> { DocCommentsBuilderUtil.TypeParamRefNameAttrName },
      
      [DocCommentsBuilderUtil.ImageTagName] = new HashSet<string> { DocCommentsBuilderUtil.ImageSourceAttrName },
      [DocCommentsBuilderUtil.InvariantTagName] = new HashSet<string> { DocCommentsBuilderUtil.InvariantNameAttrName },
      [DocCommentsBuilderUtil.ReferenceTagName] = DocCommentsBuilderUtil.PossibleReferenceTagSourceAttributes,
      
      [DocCommentsBuilderUtil.HackTagName] = new HashSet<string> { DocCommentsBuilderUtil.HackNameAttrName },
      [DocCommentsBuilderUtil.TodoTagName] = new HashSet<string> { DocCommentsBuilderUtil.TodoNameAttrName },
      [DocCommentsBuilderUtil.TicketTagName] = DocCommentsBuilderUtil.PossibleTicketAttributes
    };

  
  protected override bool AddLookupItems(DocCommentCompletionContext context, IItemsCollector collector)
  {
    var contextToken = context.ContextToken;
    if (contextToken.Parent is not IXmlTagHeader xmlTagHeader) return false;
    if (XmlTokenTypes.GetInstance(contextToken.Language) is not { } xmlTokenTypes) return false;
    if (!(contextToken.IsWhitespaceToken() || contextToken.NodeType == xmlTokenTypes.TAG_END)) return false;
    if (contextToken.GetPreviousToken() is not { } previousToken) return false;
    
    var range = new DocumentRange(context.BasicContext.CaretDocumentOffset);
    if (previousToken.NodeType == xmlTokenTypes.IDENTIFIER && previousToken.Parent is IXmlAttribute)
    {
      range = range.ExtendLeft(previousToken.GetTextLength());
    }
    
    if (!range.IsValid()) return false;
    
    var tagName = xmlTagHeader.Name.XmlName;
    if (!ourTagsPossibleAttributes.TryGetValue(tagName, out var possibleAttributes)) return false;
    
    var newSet = possibleAttributes.ToHashSet();
    newSet.ExceptWith(xmlTagHeader.Attributes.Select(attr => attr.AttributeName));
    foreach (var attribute in newSet)
    {
      var item = new CommentLookupItem($"{attribute}=\"\"", attribute, -1);
      item.InitializeRanges(new TextLookupRanges(range, range), context.BasicContext);
      collector.Add(item);
    }

    return true;
  }
}