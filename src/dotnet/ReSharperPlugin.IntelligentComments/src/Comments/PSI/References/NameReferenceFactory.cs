using System.Linq;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Resolve;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.References;

public class NameReferenceFactory : IReferenceFactory
{
  public ReferenceCollection GetReferences(ITreeNode element, ReferenceCollection oldReferences)
  {
    if (element.GetContainingFile() is not { } file) return ReferenceCollection.Empty;

    var finders = LanguageManager.Instance.TryGetCachedServices<IReferenceInCommentFinder>(element.Language);
    var references = new LocalList<IReference>();

    foreach (var finder in finders)
    {
      foreach (var referenceDescriptor in finder.FindAllReferences(element))
      {
        var treeRange = file.Translate(referenceDescriptor.Range);
        var reference = new NamedEntityReference(element, referenceDescriptor.NameWithKind, treeRange);
        references.Add(reference);
      } 
    }

    return new ReferenceCollection(references.ResultingList().ToIReadOnlyList());
  }

  public bool HasReference(ITreeNode element, IReferenceNameContainer names)
  {
    return element is IDocCommentBlock &&
           LanguageManager.Instance.TryGetCachedServices<ReferencesInDocCommentFinder>(element.Language) is { } finders &&
           finders.Any(finder => finder.FindAllReferences(element).Any());
  }
}