using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;

public record struct ReferenceInFileDescriptor([NotNull] IPsiSourceFile SourceFile, DocumentOffset Offset);

public interface IReferenceInCommentFinder
{
  [NotNull] IEnumerable<ReferenceInFileDescriptor> FindReferencesToNamedEntity(
    [NotNull] string name, NameKind nameKind, [NotNull] ITreeNode node);
}