using System.Collections.Generic;
using System.Xml;
using IntelligentComments.Comments.Calculations.Core;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using JetBrains.Util.DataStructures;

namespace IntelligentComments.Comments.PSI.DeclaredElements;

public class NamedEntityDeclaredElement : IDeclaredElement
{
  [NotNull] public ISolution Solution { get; }
  public NameWithKind NameWithKind { get; }
  public DocumentRange DeclarationRange { get; }
  public string ShortName => NameWithKind.Name;
  public bool CaseSensitiveName => true;
  public PsiLanguageType PresentationLanguage => CSharpLanguage.Instance!;


  public NamedEntityDeclaredElement(ISolution solution, NameWithKind nameWithKind, DocumentRange declarationRange)
  {
    Solution = solution;
    NameWithKind = nameWithKind;
    DeclarationRange = declarationRange;
  }
  

  public DeclaredElementType GetElementType() => new CommonDeclaredElementType("NamedEntity", null);

  public bool IsValid() => true;
  public bool IsSynthetic() => false;
  public IList<IDeclaration> GetDeclarations() => EmptyList<IDeclaration>.Instance;
  public IList<IDeclaration> GetDeclarationsIn(IPsiSourceFile sourceFile) => EmptyList<IDeclaration>.Instance;

  public HybridCollection<IPsiSourceFile> GetSourceFiles()
  {
    if (!DeclarationRange.IsValid() ||
        DeclarationRange.Document.GetPsiSourceFile(Solution) is not { } sourceFile)
    {
      return HybridCollection<IPsiSourceFile>.Empty;
    }
    
    return new HybridCollection<IPsiSourceFile>(sourceFile);
  }
  
  public bool HasDeclarationsIn(IPsiSourceFile sourceFile) => GetSourceFiles().Contains(sourceFile);
  public IPsiServices GetPsiServices() => Solution.GetPsiServices();
  public XmlNode GetXMLDoc(bool inherit) => null;
  public XmlNode GetXMLDescriptionSummary(bool inherit) => null;
}