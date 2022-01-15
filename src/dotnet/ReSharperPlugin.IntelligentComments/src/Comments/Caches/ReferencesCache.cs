using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.DocumentModel;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.TextControl;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches;

public record ReferenceInfo(IReference Reference);

[SolutionComponent]
public class ReferencesCache : AbstractOpenedDocumentBasedCache<int, ReferenceInfo>
{
  public ReferencesCache(
    Lifetime lifetime, 
    [NotNull] ITextControlManager textControlManager,
    [NotNull] IShellLocks shellLocks) 
    : base(lifetime, textControlManager, shellLocks)
  {
  }

  
  public int AddReferenceIfNotPresent(IDocument document, IReference reference)
  {
    var info = new ReferenceInfo(reference);
    int id = CreateId(document, info);

    if (TryGetValue(document, id) is { }) return id;

    return Add(document, new ReferenceInfo(reference));
  }

  protected override void BeforeRemoval(IDocument document, IEnumerable<ReferenceInfo> values)
  {
  }

  protected override int CreateId(IDocument document, ReferenceInfo value)
  {
    int documentHash = Hash.Create(document.Moniker).Value;
    int referenceHash = value.Reference.GetHashCode();
    return Hash.Combine(documentHash, referenceHash);
  }
}