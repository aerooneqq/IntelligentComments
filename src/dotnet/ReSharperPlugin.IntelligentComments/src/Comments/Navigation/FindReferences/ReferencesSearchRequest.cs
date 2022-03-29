using JetBrains.Annotations;
using JetBrains.Application.Progress;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Navigation.Requests;
using JetBrains.ReSharper.Feature.Services.Occurrences;
using JetBrains.Util;
using System.Collections;
using System.Collections.Generic;

namespace ReSharperPlugin.IntelligentComments.Comments.Navigation.FindReferences;

internal class ReferencesSearchRequest : SearchRequest
{
  [NotNull] private readonly ICollection<IOccurrence> myOccurrences;


  public override ICollection SearchTargets => EmptyArray<object>.Instance;
  public override string Title =>  "References which reference this invariant:";
  public override ISolution Solution { get; }


  public ReferencesSearchRequest([NotNull] ICollection<IOccurrence> occurrences, [NotNull] ISolution solution)
  {
    myOccurrences = occurrences;
    Solution = solution;
  }

  
  public override ICollection<IOccurrence> Search(IProgressIndicator progressIndicator)
  {
    return myOccurrences;
  }
}