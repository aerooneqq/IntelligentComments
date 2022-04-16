using JetBrains.Annotations;
using JetBrains.Application.UI.PopupLayout;
using JetBrains.DocumentModel;
using JetBrains.IDE;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Navigation.NavigationExtensions;
using JetBrains.ReSharper.Feature.Services.Occurrences;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Pointers;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.Features.Navigation;

public class NamedEntityDeclaredElementOccurence : IOccurrence
{
  [NotNull] private readonly ISolution mySolution;


  public NameWithKind NameWithKind { get; }
  public DocumentOffset DocumentOffset { get; }
  public SourceFilePtr FilePtr { get; }

  public OccurrenceType OccurrenceType => OccurrenceType.TextualOccurrence;
  public bool IsValid => true;
  public OccurrencePresentationOptions PresentationOptions { get; set; }
  

  public NamedEntityDeclaredElementOccurence(
    NameWithKind nameWithKind,
    [NotNull] ISolution solution, 
    [NotNull] IPsiSourceFile sourceFile, 
    DocumentOffset documentOffset)
  {
    NameWithKind = nameWithKind;
    mySolution = solution;
    FilePtr = sourceFile.Ptr();
    DocumentOffset = documentOffset;
    PresentationOptions = OccurrencePresentationOptions.DefaultOptions;
  }
  
  
  public bool Navigate(
    ISolution solution, PopupWindowContextSource windowContext, bool transferFocus, TabOptions tabOptions = TabOptions.Default)
  {
    var range = new TextRange(DocumentOffset.Offset);
    return FilePtr.File.Navigate(range, transferFocus, tabOptions, windowContext);
  }

  public ISolution GetSolution() => mySolution;

  public string DumpToString() => nameof(NamedEntityDeclaredElementOccurence);
}