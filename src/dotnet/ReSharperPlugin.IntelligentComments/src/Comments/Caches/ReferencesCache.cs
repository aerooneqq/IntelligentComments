using System.Collections.Generic;
using System.Threading;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.DocumentModel;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.TextControl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches;

public record ReferenceInfo(IReference Reference);

[SolutionComponent]
public class ReferencesCache : AbstractOpenedDocumentBasedCache<int, ReferenceInfo>
{
  private int myCurrentId;

  
  public ReferencesCache(
    Lifetime lifetime, 
    [NotNull] ITextControlManager textControlManager, 
    [NotNull] IShellLocks shellLocks) 
    : base(lifetime, textControlManager, shellLocks)
  {
  }

  
  public int AddReference(IDocument document, IReference reference) => Add(document, new ReferenceInfo(reference));

  protected override void BeforeRemoval(IDocument document, IEnumerable<ReferenceInfo> values)
  {
  }

  protected override int CreateId(IDocument document, ReferenceInfo value)
  {
    return Interlocked.Increment(ref myCurrentId);
  }
}