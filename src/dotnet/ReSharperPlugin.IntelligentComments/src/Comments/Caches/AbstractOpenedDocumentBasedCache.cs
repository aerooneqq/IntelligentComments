using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.DocumentModel;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.TestRunner.Abstractions.Extensions;
using JetBrains.TextControl;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches;

public abstract class AbstractOpenedDocumentBasedCache<TId, TValue> where TValue : class
{
  [NotNull] private readonly object mySyncObject = new();
  
  [NotNull] private readonly IDictionary<IDocument, IDictionary<TId, TValue>> myFilesPerDocument;


  protected AbstractOpenedDocumentBasedCache(
    Lifetime lifetime,
    [NotNull] ITextControlManager textControlManager,
    [NotNull] IThreading threading)
  {
    myFilesPerDocument = new Dictionary<IDocument, IDictionary<TId, TValue>>();
    textControlManager.TextControls.AddRemove.Advise(lifetime, args =>
    {
      threading.Queue(lifetime, $"{GetType().Name}::InvalidatingCache", () =>
      {
        if (args.IsRemoving)
        {
          lock (mySyncObject)
          {
            var allOpenedDocuments = textControlManager.TextControls.Select(editor => editor.Document).ToSet();
            var documentsToRemove = myFilesPerDocument.Keys.ToSet().Except(allOpenedDocuments);
            foreach (var document in documentsToRemove)
            {
              if (myFilesPerDocument.TryGetValue(document, out var documentEntities))
              {
                BeforeRemoval(document, documentEntities.Values);
                myFilesPerDocument.Remove(document);
              }
            }
          }
        }
      });
    });
  }


  protected abstract void BeforeRemoval(IDocument document, IEnumerable<TValue> values);

  protected TId Add([NotNull] IDocument document, [NotNull] TValue entry)
  {
    lock (mySyncObject)
    {
      var id = CreateId(document, entry);
      var documentEntities = myFilesPerDocument.GetOrCreate(document, () => new Dictionary<TId, TValue>());
      documentEntities[id] = entry;
      return id;
    }
  }
  
  protected abstract TId CreateId(IDocument document, TValue value);
  
  public TValue TryGetValue(IDocument document, TId id)
  {
    lock (mySyncObject)
    {
      if (myFilesPerDocument.TryGetValue(document, out var documentEntities) &&
          documentEntities.TryGetValue(id, out var entry))
      {
        return entry;
      }

      return null;
    }
  }
}