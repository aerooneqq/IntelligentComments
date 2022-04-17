using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.ExtensionsAPI;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Resolve;
using JetBrains.ReSharper.Psi.Search;
using JetBrains.ReSharper.Psi.Tree;
using ReSharperPlugin.IntelligentComments.Comments.PSI.DeclaredElements;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.References.Search;

public class NamedEntityReferenceSearcher : IDomainSpecificSearcher
{
  [NotNull] private readonly NamedEntityDeclaredElement myNamedEntityDeclaredElement;

  
  public NamedEntityReferenceSearcher([NotNull] NamedEntityDeclaredElement namedEntityDeclaredElement)
  {
    myNamedEntityDeclaredElement = namedEntityDeclaredElement;
  }


  public bool ProcessProjectItem<TResult>(IPsiSourceFile sourceFile, IFindResultConsumer<TResult> consumer)
  {
    if (sourceFile.GetPrimaryPsiFile() is not { } psiFile) return false;

    [NotNull]
    IEnumerable<IReference> ExtractSuitableReferences(ITreeNode node)
    {
      return node.GetReferences()
        .OfType<INamedEntityReference>()
        .Where(reference => reference.NameWithKind == myNamedEntityDeclaredElement.NameWithKind);
    }
    
    foreach (var docCommentBlock in psiFile.Descendants<IDocCommentBlock>())
    {
      foreach (var reference in ExtractSuitableReferences(docCommentBlock))
      {
        consumer.Accept(new FindResultReference(reference));
      }
    }

    foreach (var commentNode in psiFile.Descendants<ICommentNode>())
    {
      foreach (var reference in ExtractSuitableReferences(commentNode))
      {
        consumer.Accept(new FindResultReference(reference));
      }
    }

    return false;
  }

  public bool ProcessElement<TResult>(ITreeNode element, IFindResultConsumer<TResult> consumer)
  {
    return false;
  }
}