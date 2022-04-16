using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.ExtensionsAPI;
using JetBrains.ReSharper.Psi.Impl.Search.SearchDomain;
using JetBrains.ReSharper.Psi.Search;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.PSI.DeclaredElements;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.References.Search;

[PsiSharedComponent]
public class NamedEntityReferenceSearcherFactory : DomainSpecificSearcherFactoryBase
{
  [NotNull] private readonly SearchDomainFactory mySearchDomainFactory;

  
  public NamedEntityReferenceSearcherFactory([NotNull] SearchDomainFactory searchDomainFactory)
  {
    mySearchDomainFactory = searchDomainFactory;
  }
  
  
  public override bool IsCompatibleWithLanguage(PsiLanguageType languageType)
  {
    return true;
  }

  public override IDomainSpecificSearcher CreateReferenceSearcher(IDeclaredElementsSet elements, bool findCandidates)
  {
    if (elements.Count != 1) return null;
    if (elements.First() is not NamedEntityDeclaredElement namedEntityDeclaredElement) return null;
    
    return new NamedEntityReferenceSearcher(namedEntityDeclaredElement);
  }

  public override IEnumerable<string> GetAllPossibleWordsInFile(IDeclaredElement element)
  {
    if (element is not NamedEntityDeclaredElement namedEntityDeclaredElement) return EmptyList<string>.Enumerable;

    return new[] { namedEntityDeclaredElement.NameWithKind.Name };
  }

  public override ISearchDomain GetDeclaredElementSearchDomain(IDeclaredElement declaredElement)
  {
    if (declaredElement is not NamedEntityDeclaredElement namedEntityDeclaredElement) return EmptySearchDomain.Instance;
    
    return mySearchDomainFactory.CreateSearchDomain(namedEntityDeclaredElement.Solution, false);
  }
}

public class NamedEntityReferenceSearcher : IDomainSpecificSearcher
{
  [NotNull] private readonly NamedEntityDeclaredElement myNamedEntityDeclaredElement;

  
  public NamedEntityReferenceSearcher([NotNull] NamedEntityDeclaredElement namedEntityDeclaredElement)
  {
    myNamedEntityDeclaredElement = namedEntityDeclaredElement;
  }
  
  
  public bool ProcessProjectItem<TResult>(IPsiSourceFile sourceFile, IFindResultConsumer<TResult> consumer)
  {
    return false;
  }

  public bool ProcessElement<TResult>(ITreeNode element, IFindResultConsumer<TResult> consumer)
  {
    return false;
  }
}