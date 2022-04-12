using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;

public record struct NameWithKind(string Name, NameKind NameKind);

public record struct NameInFileDescriptor(
  [NotNull] IPsiSourceFile SourceFile, DocumentOffset Offset, NameWithKind NameWithKind);

public interface INamesInCommentFinder
{
  [NotNull] IEnumerable<NameInFileDescriptor> FindNames([NotNull] ITreeNode node);
}