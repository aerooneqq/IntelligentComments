using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.Util.PersistentMap;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Invariants;

[PsiComponent]
public class InvariantsNamesCache : SimpleICache<Dictionary<string, int>>
{
  [NotNull] private static readonly IUnsafeMarshaller<Dictionary<string, int>> ourMarshaller =
    UnsafeMarshallers.GetCollectionMarshaller(
      reader => new KeyValuePair<string, int>(reader.ReadString(), reader.ReadInt()),
      (writer, value) =>
      {
        writer.Write(value.Key);
        writer.Write(value.Value);
      },
      collection => new Dictionary<string, int>(collection)
    );

  
  public override string Version => "2";

  
  public InvariantsNamesCache(
    Lifetime lifetime,
    [NotNull] IShellLocks locks,
    [NotNull] IPersistentIndexManager persistentIndexManager)
    : base(lifetime, locks, persistentIndexManager, ourMarshaller)
  {
  }
  

  public override object Build(IPsiSourceFile sourceFile, bool isStartup)
  {
    var invariants = new Dictionary<string, int>();
    foreach (var file in sourceFile.GetPsiFiles<KnownLanguage>())
    {
      GetProcessor(file.Language)?.Process(file, invariants);
    }

    return invariants;
  }

  [CanBeNull]
  private static IInvariantsProcessor GetProcessor([NotNull] PsiLanguageType languageType) =>
    LanguageManager.Instance.TryGetService<IInvariantsProcessor>(languageType);
}