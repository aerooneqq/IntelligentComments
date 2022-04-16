using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

[Language(typeof(KnownLanguage))]
public class ReferencesInDocCommentFinder : IReferenceInCommentFinder
{
  public IEnumerable<ReferenceInFileDescriptor> FindReferencesToNamedEntity(NameWithKind nameWithKind, ITreeNode node)
  {
    return FindReferencesToNamedEntityOrAll(node, nameWithKind);
  }

  private static IEnumerable<ReferenceInFileDescriptor> FindReferencesToNamedEntityOrAll(
    [NotNull] ITreeNode node,
    NameWithKind? nameWithKind)
  {
    if (node is not IDocCommentBlock docComment || node.GetSourceFile() is not { } sourceFile)
    {
      return EmptyList<ReferenceInFileDescriptor>.Enumerable;
    }

    var references = new LocalList<ReferenceInFileDescriptor>();
    docComment.ExecuteWithReferences(referenceTag =>
    {
      var extraction = DocCommentsBuilderUtil.TryExtractOneReferenceNameKindFromReferenceTag(referenceTag);
      if (extraction is null || (nameWithKind.HasValue && extraction != nameWithKind.Value)) return;
      if (DocCommentsBuilderUtil.TryGetOneReferenceSourceAttribute(referenceTag) is not { } sourceAttribute) return;
      
      references.Add(new ReferenceInFileDescriptor(extraction.Value, sourceFile, sourceAttribute.Value.GetDocumentRange()));
    });

    return references.ResultingList();
  }

  public IEnumerable<ReferenceInFileDescriptor> FindAllReferences([NotNull] ITreeNode node)
  {
    return FindReferencesToNamedEntityOrAll(node, null);
  }
}