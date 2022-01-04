using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.DocumentManagers;
using JetBrains.DocumentModel;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.RdBackend.Common.Features.Languages;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Files.SandboxFiles;
using JetBrains.Rider.Model;
using JetBrains.TextControl;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;
using ReSharperPlugin.IntelligentComments.Comments.CodeFragmentsHighlighting;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches;

public record SandboxFileInfo(LifetimeDefinition LifetimeDefinition, SandboxPsiSourceFile SandboxPsiSourceFile);

[SolutionComponent]
public class SandboxesCache : AbstractVisibleDocumentBasedCache<string, SandboxFileInfo>
{
  private readonly Lifetime myLifetime;
  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly ISolution mySolution;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly ISandboxDocumentsHelper myHelper;

  
  public SandboxesCache(
    Lifetime lifetime,
    [NotNull] ILogger logger,
    ISolution solution,
    [NotNull] ITextControlManager textControlManager, 
    [NotNull] IShellLocks shellLocks,
    [NotNull] ISandboxDocumentsHelper helper) 
    : base(lifetime, textControlManager, shellLocks)
  {
    myLifetime = lifetime;
    myLogger = logger;
    mySolution = solution;
    myShellLocks = shellLocks;
    myHelper = helper;
  }
  
  
  [CanBeNull]
  public SandboxPsiSourceFile TryGetSandboxPsiSourceFile(IDocument originalDocument, string fileName)
  {
    return this[originalDocument, fileName]?.SandboxPsiSourceFile;
  }

  public SandboxPsiSourceFile CreateSandboxFileFor(IDocument originalDocument, CodeHighlightingRequest request)
  {
    myShellLocks.AssertMainThread();
    var lifetimeDef = myLifetime.CreateNested();
    var highlightingLifetime = lifetimeDef.Lifetime;
    var sandBoxInfo = CreateSandboxInfo(request);
    var sandboxFile = myHelper.GetOrCreateSandboxProjectFile(request.DocumentId, sandBoxInfo, highlightingLifetime);

    var document = sandboxFile.GetDocument();
    if (document is not RiderDocument riderDocument)
    {
      myLogger.LogAssertion($"Got unexpected document for {sandboxFile}: {document}");
      return null;
    }

    var viewModel = new SandBoxDocumentModel(sandBoxInfo);
    myHelper.InitSandboxDocument(
      request.DocumentId,
      viewModel,
      highlightingLifetime, 
      riderDocument,
      sandboxFile,
      DocumentHostBase.GetInstance(sandboxFile.GetSolution()));

    var sourceFile = riderDocument.GetPsiSourceFile(mySolution);
    if (sourceFile is not SandboxPsiSourceFile sandboxPsiSourceFile)
    {
      myLogger.LogAssertion($"Got unexpected sourceFile for {sandboxFile}: {sourceFile}");
      return null;
    }
    
    Add(originalDocument, new SandboxFileInfo(lifetimeDef, sandboxPsiSourceFile));

    return sandboxPsiSourceFile;
  }
  
  private static SandboxInfo CreateSandboxInfo(CodeHighlightingRequest request)
  {
    var documentText = request.CreateDocumentText();
    return new SandboxInfo(
      request.DocumentId,
      documentText,
      new RdTextRange(documentText.Length, documentText.Length),
      true,
      null,
      new List<string>(),
      true,
      new List<CompletionItemType>
      {
        CompletionItemType.Default,
        CompletionItemType.NamedParameter,
        CompletionItemType.PostfixTemplate,
        CompletionItemType.TemplateItem
      },
      request.Language.ToRdLanguage(),
      true,
      null,
      true,
      new List<char>()
    );
  }

  protected override void BeforeRemoval(IDocument document, IEnumerable<SandboxFileInfo> values)
  {
    myShellLocks.AssertMainThread();
    foreach (var value in values)
    {
      value.LifetimeDefinition.Terminate();
    }
  }

  protected override string CreateId(SandboxFileInfo value)
  {
    return value.SandboxPsiSourceFile.ProjectFile.GetPersistentID();
  }
}