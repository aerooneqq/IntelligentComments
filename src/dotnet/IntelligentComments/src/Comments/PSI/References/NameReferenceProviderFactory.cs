using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.DataFlow;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Caches;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Resolve;
using JetBrains.ReSharper.Psi.Tree;

namespace IntelligentComments.Comments.PSI.References;

[ReferenceProviderFactory]
public class NameReferenceProviderFactory : IReferenceProviderFactory
{
  [NotNull] private readonly ICommentsSettings mySettings;
  
  
  public ISignal<IReferenceProviderFactory> Changed { get; }

  
  public NameReferenceProviderFactory(Lifetime lifetime, [NotNull] ICommentsSettings settings)
  {
    mySettings = settings;
    Changed = new Signal<IReferenceProviderFactory>(lifetime, $"{GetType().Name}::{nameof(Changed)}");
  }
  
  
  public IReferenceFactory CreateFactory(IPsiSourceFile sourceFile, IFile file, IWordIndex wordIndexForChecks)
  {
    if (!mySettings.ExperimentalFeaturesEnabled.Value || file is not ICSharpFile) return null;

    return new NameReferenceFactory();
  }
}