using System.Collections.Generic;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

[Language(typeof(KnownLanguage))]
public class ReferencesInDocCommentFinder : IReferenceInCommentFinder
{
  public IEnumerable<ReferenceInFileDescriptor> FindReferencesToInvariant(string invariantName, ITreeNode node)
  {
    if (node is not IDocCommentBlock docComment || node.GetSourceFile() is not { } sourceFile)
    {
      return EmptyList<ReferenceInFileDescriptor>.Enumerable;
    }

    var references = new LocalList<ReferenceInFileDescriptor>();
    docComment.ExecuteWithReferences(referenceTag =>
    {
      var invariantReferenceSourceAttr = DocCommentsBuilderUtil.TryGetInvariantReferenceSourceAttribute(referenceTag);
      if (invariantReferenceSourceAttr is null || invariantReferenceSourceAttr.UnquotedValue != invariantName) return;

      var offset = invariantReferenceSourceAttr.GetDocumentStartOffset();
      references.Add(new ReferenceInFileDescriptor(sourceFile, offset));
    });

    return references.ResultingList();
  }
}