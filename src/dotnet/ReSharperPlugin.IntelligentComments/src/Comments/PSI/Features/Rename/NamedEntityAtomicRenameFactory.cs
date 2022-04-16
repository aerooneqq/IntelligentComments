using System.Collections.Generic;
using JetBrains.Application.Progress;
using JetBrains.ReSharper.Feature.Services.Refactorings;
using JetBrains.ReSharper.Feature.Services.Refactorings.Specific.Rename;
using JetBrains.ReSharper.Psi;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.PSI.DeclaredElements;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.Features.Rename;

public class NamedEntityAtomicRenameFactory : AtomicRenamesFactory
{
  public override bool IsApplicable(IDeclaredElement declaredElement) => declaredElement is NamedEntityDeclaredElement;

  public override RenameAvailabilityCheckResult CheckRenameAvailability(IDeclaredElement declaredElement)
  {
    return declaredElement switch
    {
      NamedEntityDeclaredElement => RenameAvailabilityCheckResult.CanBeRenamed,
      _ => RenameAvailabilityCheckResult.CanNotBeRenamed
    };
  }

  public override IEnumerable<AtomicRenameBase> CreateAtomicRenames(
    IDeclaredElement declaredElement, string newName, bool doNotAddBindingConflicts)
  {
    if (declaredElement is not NamedEntityDeclaredElement namedEntityDeclaredElement) 
      return EmptyList<AtomicRenameBase>.Enumerable;
    
    return new[] { new NamedEntityAtomicRename(newName, namedEntityDeclaredElement) };
  }
}

public class NamedEntityAtomicRename : AtomicRenameBase
{
  public override string NewName { get; }
  public override string OldName { get; }
  
  
  public NamedEntityAtomicRename(string newName, NamedEntityDeclaredElement declaredElement)
  {
    NewName = newName;
    OldName = declaredElement.NameWithKind.Name;
  }
  

  public override void Rename(
    IRenameRefactoring executer, IProgressIndicator pi, bool hasConflictsWithDeclarations, IRefactoringDriver driver)
  {
    
  }

  public override IDeclaredElement NewDeclaredElement => throw new System.NotImplementedException();
  

  public override IDeclaredElement PrimaryDeclaredElement => throw new System.NotImplementedException();

  public override IList<IDeclaredElement> SecondaryDeclaredElements => throw new System.NotImplementedException();
}