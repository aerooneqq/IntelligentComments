using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.DataFlow;
using JetBrains.DocumentModel;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.TestRunner.Abstractions.Extensions;
using JetBrains.TextControl;

namespace IntelligentComments.Comments.Caches;

public abstract class AbstractOpenedDocumentBasedCache<TId, TValue> where TValue : class
{
  private readonly Lifetime myLifetime;
  [NotNull] private readonly ITextControlManager myTextControlManager;
  [NotNull] private readonly IThreading myThreading;
  [NotNull] private readonly object mySyncObject = new();
  [NotNull] private readonly IDictionary<IDocument, IDictionary<TId, TValue>> myCachesPerDocument;


  protected AbstractOpenedDocumentBasedCache(
    Lifetime lifetime,
    [NotNull] ITextControlManager textControlManager,
    [NotNull] IThreading threading)
  {
    myLifetime = lifetime;
    myTextControlManager = textControlManager;
    myThreading = threading;
    myCachesPerDocument = new Dictionary<IDocument, IDictionary<TId, TValue>>();
    textControlManager.TextControls.AddRemove.Advise(lifetime, HandleTextControlAddRemove);
  }

  
  private void HandleTextControlAddRemove([NotNull] AddRemoveEventArgs<ITextControl> args)
  {
    if (!args.IsRemoving || !myLifetime.IsAlive) return;
      
    myThreading.Queue(myLifetime, $"{GetType().Name}::InvalidatingCache", () =>
    {
      myLifetime.TryExecute(Invalidate);
    });
  }

  private void Invalidate()
  {
    lock (mySyncObject)
    {
      var allOpenedDocuments = myTextControlManager.TextControls.Select(editor => editor.Document).ToSet();
      var documentsToRemove = myCachesPerDocument.Keys.ToSet().Except(allOpenedDocuments);
      foreach (var document in documentsToRemove)
      {
        if (myCachesPerDocument.TryGetValue(document, out var documentEntities))
        {
          BeforeRemoval(document, documentEntities.Values);
          myCachesPerDocument.Remove(document);
        }
      }
    }
  }

  protected abstract void BeforeRemoval(IDocument document, IEnumerable<TValue> values);

  protected TId Add([NotNull] IDocument document, [NotNull] TValue entry)
  {
    lock (mySyncObject)
    {
      var id = CreateId(document, entry);
      var documentEntities = myCachesPerDocument.GetOrCreate(document, static () => new Dictionary<TId, TValue>());
      documentEntities[id] = entry;
      return id;
    }
  }
  
  protected abstract TId CreateId(IDocument document, TValue value);
  
  public TValue TryGetValue(IDocument document, TId id)
  {
    lock (mySyncObject)
    {
      if (myCachesPerDocument.TryGetValue(document, out var documentEntities) &&
          documentEntities.TryGetValue(id, out var entry))
      {
        return entry;
      }

      return null;
    }
  }
}