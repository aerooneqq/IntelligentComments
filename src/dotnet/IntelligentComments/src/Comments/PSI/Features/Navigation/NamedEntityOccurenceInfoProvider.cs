using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Occurrences;
using JetBrains.ReSharper.Feature.Services.Occurrences.OccurrenceInformation;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Pointers;
using JetBrains.Util;

namespace IntelligentComments.Comments.PSI.Features.Navigation;

[SolutionFeaturePart]
public class NamedEntityOccurenceInfoProvider : IOccurrenceInformationProvider
{
  public ProjectModelElementEnvoy GetProjectModelElementEnvoy(IOccurrence occurrence) => null;
  public IDeclaredElementEnvoy GetTypeMember(IOccurrence occurrence) => null;
  public IDeclaredElementEnvoy GetTypeElement(IOccurrence occurrence) => null;
  public IDeclaredElementEnvoy GetNamespace(IOccurrence occurrence) => null;
  public OccurrenceMergeContext GetMergeContext(IOccurrence occurrence) => new OccurrenceMergeContext(occurrence);

  
  public TextRange GetTextRange(IOccurrence occurrence)
  {
    if (occurrence is not NamedEntityDeclaredElementOccurence namedEntityDeclaredElementOccurence)
      return TextRange.InvalidRange;

    return new TextRange(namedEntityDeclaredElementOccurence.DocumentOffset.Offset);
  }

  public SourceFilePtr GetSourceFilePtr(IOccurrence occurrence)
  {
    if (occurrence is not NamedEntityDeclaredElementOccurence namedEntityDeclaredElementOccurence)
      return SourceFilePtr.Fake;

    return namedEntityDeclaredElementOccurence.FilePtr;
  }

  public bool IsApplicable(IOccurrence occurrence) => occurrence is NamedEntityDeclaredElementOccurence;
}