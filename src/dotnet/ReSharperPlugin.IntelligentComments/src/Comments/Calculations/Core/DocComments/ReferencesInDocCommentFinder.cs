using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

[Language(typeof(KnownLanguage))]
public class ReferencesInDocCommentFinder : INamedEntitiesCommonFinder
{
  public IEnumerable<CommonNamedEntityDescriptor> FindReferences(ITreeNode node, NameWithKind nameWithKind)
  {
    return FindReferencesToNamedEntityOrAll(node, nameWithKind);
  }

  private static IEnumerable<CommonNamedEntityDescriptor> FindReferencesToNamedEntityOrAll(
    [NotNull] ITreeNode node,
    NameWithKind? nameWithKind)
  {
    if (node is not IDocCommentBlock docComment || node.GetSourceFile() is not { } sourceFile)
    {
      return EmptyList<CommonNamedEntityDescriptor>.Enumerable;
    }

    var references = new LocalList<CommonNamedEntityDescriptor>();
    docComment.ExecuteWithReferences(referenceTag =>
    {
      var extraction = DocCommentsBuilderUtil.TryExtractOneReferenceNameKindFromReferenceTag(referenceTag);
      if (extraction is null || (nameWithKind.HasValue && extraction != nameWithKind.Value)) return;
      if (DocCommentsBuilderUtil.TryGetOneReferenceSourceAttribute(referenceTag) is not { } sourceAttribute) return;
      
      references.Add(new CommonNamedEntityDescriptor(sourceFile, sourceAttribute.Value.GetDocumentRange(), extraction.Value));
    });

    return references.ResultingList();
  }

  public IEnumerable<CommonNamedEntityDescriptor> FindAllReferences([NotNull] ITreeNode node)
  {
    return FindReferencesToNamedEntityOrAll(node, null);
  }

  public IEnumerable<CommonNamedEntityDescriptor> FindNames(ITreeNode node)
  {
    return EmptyList<CommonNamedEntityDescriptor>.Enumerable;
  }
}