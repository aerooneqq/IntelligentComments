using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.DocumentModel;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.TestRunner.Abstractions.Extensions;
using JetBrains.TextControl;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches;

public abstract class AbstractVisibleDocumentBasedCache<TValue> where TValue : class
{
  [NotNull] private readonly object mySyncObject = new();
  
  [NotNull] private readonly IDictionary<IDocument, IDictionary<string, TValue>> myFilesPerDocument;


  protected AbstractVisibleDocumentBasedCache(
    Lifetime lifetime,
    [NotNull] ITextControlManager textControlManager,
    [NotNull] IShellLocks shellLocks)
  {
    myFilesPerDocument = new Dictionary<IDocument, IDictionary<string, TValue>>();
    textControlManager.VisibleTextControls.AddRemove.Advise(lifetime, args =>
    {
      shellLocks.Queue(lifetime, $"{GetType().Name}::InvalidatingCache", () =>
      {
        if (args.IsRemoving)
        {
          lock (mySyncObject)
          {
            var allOpenedDocuments = textControlManager.VisibleTextControls.Select(editor => editor.Document).ToSet();
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

  protected void Add([NotNull] IDocument document, [NotNull] TValue entry)
  {
    lock (mySyncObject)
    {
      var id = CreateId(entry);
      var documentEntities = myFilesPerDocument.GetOrCreate(document, () => new Dictionary<string, TValue>());
      documentEntities[id] = entry;
    }
  }
  
  protected abstract string CreateId(TValue value);
  
  [CanBeNull] 
  public TValue this[IDocument document, string id]
  {
    get
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
}