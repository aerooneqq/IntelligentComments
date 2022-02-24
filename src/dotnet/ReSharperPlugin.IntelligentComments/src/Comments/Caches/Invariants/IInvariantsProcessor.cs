using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;

public interface IInvariantsProcessor
{
  void Process([NotNull] IFile file, [NotNull] Dictionary<string, int> invariantsCount);
}