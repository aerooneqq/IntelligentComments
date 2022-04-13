using System.Collections.Generic;
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
    if (node is not IDocCommentBlock docComment || node.GetSourceFile() is not { } sourceFile)
    {
      return EmptyList<ReferenceInFileDescriptor>.Enumerable;
    }

    var references = new LocalList<ReferenceInFileDescriptor>();
    docComment.ExecuteWithReferences(referenceTag =>
    {
      var extraction = DocCommentsBuilderUtil.TryExtractOneReferenceNameKindFromReferenceTag(referenceTag);
      if (extraction is null || extraction != nameWithKind) return;

      var offset = referenceTag.GetDocumentStartOffset();
      references.Add(new ReferenceInFileDescriptor(sourceFile, offset));
    });

    return references.ResultingList();
  }
}