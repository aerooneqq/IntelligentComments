using System.Collections.Generic;
using IntelligentComments.Comments.Calculations.Core;
using IntelligentComments.Comments.Calculations.Core.DocComments.Utils;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;

namespace IntelligentComments.Comments.Caches.Names;

public record struct NamedEntityInfo(DocumentOffset Offset);

/// <summary>
/// Names processor is responsible for processing the IFile and finding all declarations of names. The info about name
/// contains the offset in the source file <see cref="NamedEntityInfo"/>.
/// </summary>
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
        if (!IsDescriptorSuitable(descriptor))
        {
          continue;
        }
        
        var infos = context.GetOrCreateValue(descriptor.NameWithKind.Name, static () => new List<NamedEntityInfo>());
        infos.Add(new NamedEntityInfo(element.GetDocumentStartOffset()));
      }
    }
  }

  private bool IsDescriptorSuitable(CommonNamedEntityDescriptor descriptor)
  {
    return !(descriptor.NameWithKind.NameKind != myWantedNameKind ||
           descriptor.NameWithKind.Name.IsNullOrEmpty() ||
           descriptor.NameWithKind.Name.IsNullOrWhitespace());
  }
  
  public void ProcessAfterInterior(ITreeNode element, Dictionary<string, List<NamedEntityInfo>> context)
  {
  }
  
  public bool InteriorShouldBeProcessed(ITreeNode element, Dictionary<string, List<NamedEntityInfo>> context) => true;
  public bool IsProcessingFinished(Dictionary<string, List<NamedEntityInfo>> context) => false;
}