using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;

public record struct ReferenceInFileDescriptor(
  NameWithKind NameWithKind,
  [NotNull] IPsiSourceFile SourceFile, 
  DocumentRange Range);

public interface IReferenceInCommentFinder
{
  [NotNull] IEnumerable<ReferenceInFileDescriptor> FindReferencesToNamedEntity(
    NameWithKind nameWithKind, [NotNull] ITreeNode node);

  [NotNull] IEnumerable<ReferenceInFileDescriptor> FindAllReferences(ITreeNode node);
}