using System.Collections.Generic;
using System.Xml;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using JetBrains.Util.DataStructures;
using ReSharperPlugin.IntelligentComments.Comments.Caches.Names;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.DeclaredElements;

public class NamedEntityDeclaredElement : IDeclaredElement
{
  [NotNull] private readonly INamesCache myCache;
  [NotNull] private readonly ISolution mySolution;

  
  public NameWithKind NameWithKind { get; }
  public DocumentOffset DeclarationOffset { get; }


  public NamedEntityDeclaredElement(ISolution solution, NameWithKind nameWithKind, DocumentOffset declarationOffset)
  {
    mySolution = solution;
    NameWithKind = nameWithKind;
    DeclarationOffset = declarationOffset;
    myCache = NamesCacheUtil.GetCacheFor(solution, nameWithKind.NameKind);
  }



  public DeclaredElementType GetElementType()
  {
    return new CommonDeclaredElementType("NamedEntity", null);
  }

  public bool IsValid()
  {
    return true;
  }

  public bool IsSynthetic()
  {
    return false;
  }

  public IList<IDeclaration> GetDeclarations()
  {
    return EmptyList<IDeclaration>.Instance;
  }

  public IList<IDeclaration> GetDeclarationsIn(IPsiSourceFile sourceFile)
  {
    return EmptyList<IDeclaration>.Instance;
  }

  public HybridCollection<IPsiSourceFile> GetSourceFiles()
  {
    return HybridCollection<IPsiSourceFile>.Empty;
  }

  public bool HasDeclarationsIn(IPsiSourceFile sourceFile)
  {
    return false;
  }

  public IPsiServices GetPsiServices()
  {
    return mySolution.GetPsiServices();
  }

  public XmlNode GetXMLDoc(bool inherit) => null;

  public XmlNode GetXMLDescriptionSummary(bool inherit) => null;

  public string ShortName => NameWithKind.Name;

  public bool CaseSensitiveName => true;

  public PsiLanguageType PresentationLanguage => CSharpLanguage.Instance!;
}