using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;

public record struct NameWithKind([NotNull] string Name, NameKind NameKind);

public record struct CommonNamedEntityDescriptor(
  [NotNull] IPsiSourceFile SourceFile, DocumentRange EntityRange, NameWithKind NameWithKind);

public interface INamedEntitiesCommonFinder
{
  [NotNull] 
  IEnumerable<CommonNamedEntityDescriptor> FindReferences([NotNull] ITreeNode node, NameWithKind nameWithKind);

  [NotNull]
  IEnumerable<CommonNamedEntityDescriptor> FindAllReferences([NotNull] ITreeNode node);

  [NotNull] 
  IEnumerable<CommonNamedEntityDescriptor> FindNames([NotNull] ITreeNode node);
}