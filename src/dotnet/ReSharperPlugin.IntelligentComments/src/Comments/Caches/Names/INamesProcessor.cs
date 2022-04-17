using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Names;

public record struct NamedEntityInfo(DocumentOffset Offset);

public interface INamesProcessor
{
  void Process([NotNull] IFile file, [NotNull] Dictionary<string, List<NamedEntityInfo>> namesInfo);
}

public class NamesProcessor : INamesProcessor, IRecursiveElementProcessor<Dictionary<string, List<NamedEntityInfo>>>
{
  private readonly NameKind myWantedNameKind;

  
  public NamesProcessor(NameKind wantedNameKind)
  {
    myWantedNameKind = wantedNameKind;
  }
  
  
  public void Process(IFile file, Dictionary<string, List<NamedEntityInfo>> namesInfo)
  {
    file.ProcessThisAndDescendants(this, namesInfo);
  }

  public void ProcessBeforeInterior(ITreeNode element, Dictionary<string, List<NamedEntityInfo>> context)
  {
    var finders = LanguageManager.Instance.TryGetCachedServices<INamedEntitiesCommonFinder>(element.Language);
    foreach (var finder in finders)
    {
      foreach (var descriptor in finder.FindNames(element))
      {
        if (descriptor.NameWithKind.NameKind != myWantedNameKind) continue;
        
        var infos = context.GetOrCreateValue(descriptor.NameWithKind.Name, static () => new List<NamedEntityInfo>());
        infos.Add(new NamedEntityInfo(element.GetDocumentStartOffset()));
      }
    }
  }
  
  public void ProcessAfterInterior(ITreeNode element, Dictionary<string, List<NamedEntityInfo>> context)
  {
  }
  
  public bool InteriorShouldBeProcessed(ITreeNode element, Dictionary<string, List<NamedEntityInfo>> context) => true;
  public bool IsProcessingFinished(Dictionary<string, List<NamedEntityInfo>> context) => false;
}