using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Visitors;

public record struct ReferenceInFileDescriptor([NotNull] IPsiSourceFile SourceFile, DocumentOffset Offset);

public interface IReferenceInCommentFinder
{
  [NotNull] IEnumerable<ReferenceInFileDescriptor> FindReferencesToInvariant(
    [NotNull] string invariantName, [NotNull] ITreeNode node);
}