using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Application;
using JetBrains.Application.Progress;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Refactorings;
using JetBrains.ReSharper.Feature.Services.Refactorings.Specific.Rename;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.ExtensionsAPI;
using JetBrains.ReSharper.Psi.ExtensionsAPI.Tree;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Impl.CodeStyle;
using JetBrains.ReSharper.Psi.Parsing;
using JetBrains.ReSharper.Psi.Pointers;
using JetBrains.ReSharper.Psi.Search;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Parsing;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.Text;
using JetBrains.Util;
using JetBrains.Util.Logging;
using ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp.DocComments;
using ReSharperPlugin.IntelligentComments.Comments.PSI.DeclaredElements;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.Features.Rename;

[ShellFeaturePart]
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
  [NotNull] private static readonly ILogger ourLogger = Logger.GetLogger<NamedEntityAtomicRename>();
  
    
  [NotNull] private readonly ISolution mySolution;
  [NotNull] private readonly IDeclaredElementPointer<NamedEntityDeclaredElement> myOldElementPointer;
  [CanBeNull] private IDeclaredElementPointer<NamedEntityDeclaredElement> myNewElementPointer;

  
  public override string NewName { get; }
  public override string OldName { get; }
  public override IDeclaredElement NewDeclaredElement => myNewElementPointer.FindDeclaredElement();
  public override IDeclaredElement PrimaryDeclaredElement => myOldElementPointer.FindDeclaredElement();
  public override IList<IDeclaredElement> SecondaryDeclaredElements => EmptyList<IDeclaredElement>.Instance;
  
  
  public NamedEntityAtomicRename([NotNull] string newName, [NotNull] NamedEntityDeclaredElement declaredElement)
  {
    mySolution = declaredElement.Solution;
    myOldElementPointer = declaredElement.CreateElementPointer();
    NewName = newName;
    OldName = declaredElement.NameWithKind.Name;
  }
  

  public override void Rename(
    IRenameRefactoring executer, IProgressIndicator pi, bool hasConflictsWithDeclarations, IRefactoringDriver driver)
  {
    if (myOldElementPointer.FindDeclaredElement() is not { } oldElement) return;
    
    var declarationRange = oldElement.DeclarationRange;
    if (declarationRange.Document.GetPsiSourceFile(mySolution) is not { } sourceFile) return;
    if (sourceFile.GetPrimaryPsiFile() is not { } file) return;

    var translatedRange = file.Translate(declarationRange.StartOffset);
    if (file.FindTokenAt(translatedRange) is not { } token) return;
    if (!token.GetDocumentRange().Contains(declarationRange)) return;

    DocumentRange newDeclarationRange;
    if (token.TryFindDocCommentBlock() is { } docCommentBlock)
    {
      if (LanguageManager.Instance.TryGetService<IPsiHelper>(token.Language) is not { } helper) return;
      if (helper.GetXmlDocPsi(docCommentBlock) is not { XmlFile: { } xmlFile }) return;

      var docRange = xmlFile.Translate(declarationRange.StartOffset);
      if (xmlFile.FindTokenAt(docRange) is not IXmlValueToken { Parent: IXmlAttribute } valueToken)
      {
        ourLogger.LogAssertion($"The found token was not IXmlValueToken for {docRange}");
        return;
      }
      
      var newValue = RenameUtil.ReplaceAttributeValue(valueToken, NewName);
      newDeclarationRange = newValue.GetDocumentRange();
    }
    else if (token is ICommentNode commentNode)
    {
      if (RenameUtil.ReplaceNameCommentNode(commentNode, NewName) is not { } newNameRange) return;
      newDeclarationRange = newNameRange;
    }
    else
    {
      return;
    }

    var newNameWithKind = oldElement.NameWithKind with { Name = NewName };
    var newDeclaredElement = new NamedEntityDeclaredElement(mySolution, newNameWithKind, newDeclarationRange);
    var searchDomain = mySolution.GetComponent<SearchDomainFactory>().CreateSearchDomain(mySolution, false);
    var references = mySolution.GetPsiServices().Finder.FindReferences(oldElement, searchDomain, pi);
      
    foreach (var reference in references)
    {
      reference.BindTo(newDeclaredElement);
    }

    myNewElementPointer = newDeclaredElement.CreateElementPointer();
    mySolution.GetPsiServices().Caches.Update();
  }
}