using System.Collections.Generic;
using IntelligentComments.Comments.Domain.Core.References;
using JetBrains.Annotations;
using JetBrains.Application.Parts;
using JetBrains.Application.Threading;
using JetBrains.DocumentModel;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.TextControl;
using JetBrains.Util;

namespace IntelligentComments.Comments.Caches;

public record ReferenceInfo([NotNull] IDomainReference DomainReference);

[SolutionComponent(Instantiation.DemandAnyThreadSafe)]
public class ReferencesCache : AbstractOpenedDocumentBasedCache<int, ReferenceInfo>
{
  public ReferencesCache(
    Lifetime lifetime,
    [NotNull] ITextControlManager textControlManager,
    [NotNull] IShellLocks shellLocks)
    : base(lifetime, textControlManager, shellLocks)
  {
  }

  
  public int AddReferenceIfNotPresent(IDocument document, IDomainReference domainReference)
  {
    var info = new ReferenceInfo(domainReference);
    var id = CreateId(document, info);

    if (TryGetValue(document, id) is { }) return id;

    return Add(document, info);
  }

  protected override void BeforeRemoval(IDocument document, IEnumerable<ReferenceInfo> values)
  {
  }

  protected override int CreateId(IDocument document, ReferenceInfo value)
  {
    var documentHash = Hash.Create(document.Moniker).Value;
    var referenceHash = value.DomainReference.GetHashCode();
    return Hash.Combine(documentHash, referenceHash);
  }
}