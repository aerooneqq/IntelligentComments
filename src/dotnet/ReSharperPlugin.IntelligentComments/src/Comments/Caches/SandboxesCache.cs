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
using JetBrains.Rider.Backend.Features.Documents;
using JetBrains.Rider.Model;
using JetBrains.TextControl;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;
using ReSharperPlugin.IntelligentComments.Comments.CodeFragmentsHighlighting;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches;

public record SandboxFileInfo(
  [NotNull] LifetimeDefinition LifetimeDefinition,
  [NotNull] SandboxPsiSourceFile SandboxPsiSourceFile,
  [NotNull] IDictionary<int, TextRange> TextHashesToOffset);

public record SandboxCodeFragmentInfo([NotNull] SandboxPsiSourceFile SourceFile, int StartOffset, int EndOffset);

[SolutionComponent]
public class SandboxesCache : AbstractOpenedDocumentBasedCache<string, SandboxFileInfo>
{
  private readonly Lifetime myLifetime;
  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly ISolution mySolution;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly ISandboxDocumentsHelper myHelper;
  [NotNull] private readonly RiderDocumentHost myDocumentHost;


  public SandboxesCache(
    Lifetime lifetime,
    [NotNull] ILogger logger,
    ISolution solution,
    [NotNull] ITextControlManager textControlManager, 
    [NotNull] IShellLocks shellLocks,
    [NotNull] ISandboxDocumentsHelper helper,
    [NotNull] RiderDocumentHost documentHost) 
    : base(lifetime, textControlManager, shellLocks)
  {
    myLifetime = lifetime;
    myLogger = logger;
    mySolution = solution;
    myShellLocks = shellLocks;
    myHelper = helper;
    myDocumentHost = documentHost;
  }
  
  
  [CanBeNull]
  public SandboxPsiSourceFile TryGetSandboxPsiSourceFile(IDocument originalDocument, string fileName)
  {
    return TryGetValue(originalDocument, fileName)?.SandboxPsiSourceFile;
  }

  public SandboxCodeFragmentInfo GetOrCreateSandboxFileForHighlighting(
    [NotNull] IDocument originalDocument,
    [NotNull] CodeHighlightingRequest request)
  {
    myShellLocks.AssertMainThread();
    LifetimeDefinition lifetimeDef = myLifetime.CreateNested();
    Lifetime highlightingLifetime = lifetimeDef.Lifetime;
    if (request.Document.GetData(DocumentHostBase.DocumentIdKey) is not { } documentId)
    {
      myLogger.Error($"Failed to get documentId for {request.Document.Moniker}");
      return null;
    }
    
    SandboxInfo sandBoxInfo = CreateSandboxInfo(request, documentId);
    IProjectFile sandboxFile = myHelper.GetOrCreateSandboxProjectFile(documentId, sandBoxInfo, highlightingLifetime);
    
    if (TryGetValue(originalDocument, originalDocument.Moniker) is { } existingSandboxFileInfo)
    {
      return AddTextIfNeededAndGetFragment(existingSandboxFileInfo, request);
    }
    
    IDocument document = sandboxFile.GetDocument();
    if (document is not RiderDocument riderDocument)
    {
      myLogger.LogAssertion($"Got unexpected document for {sandboxFile}: {document}");
      return null;
    }

    var viewModel = new SandBoxDocumentModel(sandBoxInfo);
    myHelper.InitSandboxDocument(
      documentId,
      viewModel,
      highlightingLifetime, 
      riderDocument,
      sandboxFile,
      myDocumentHost);

    IPsiSourceFile sourceFile = riderDocument.GetPsiSourceFile(mySolution);
    if (sourceFile is not SandboxPsiSourceFile sandboxPsiSourceFile)
    {
      myLogger.LogAssertion($"Got unexpected sourceFile for {sandboxFile}: {sourceFile}");
      return null;
    }

    int endOffset = riderDocument.GetTextLength();
    var idToOffsets = new Dictionary<int, TextRange>
    {
      [request.CalculateTextHash()] = new(0, endOffset)
    };
    
    Add(originalDocument, new SandboxFileInfo(lifetimeDef, sandboxPsiSourceFile, idToOffsets));

    return new SandboxCodeFragmentInfo(sandboxPsiSourceFile, 0, endOffset);
  }
  
  [NotNull]
  private static SandboxCodeFragmentInfo AddTextIfNeededAndGetFragment(
    [NotNull] SandboxFileInfo sandboxFileInfo,
    [NotNull] CodeHighlightingRequest request)
  {
    (_, SandboxPsiSourceFile sandboxPsiSourceFile, IDictionary<int, TextRange> textHashesToOffset) = sandboxFileInfo;
    if (textHashesToOffset.TryGetValue(request.CalculateTextHash(), out TextRange existingRange))
    {
      return new SandboxCodeFragmentInfo(sandboxPsiSourceFile, existingRange.StartOffset, existingRange.EndOffset);
    }
    
    IDocument sandboxDocument = sandboxPsiSourceFile.Document;
    int startOffset = sandboxDocument.GetTextLength();
    string createdText = request.Text;
    int endOffset = startOffset + createdText.Length;
    
    sandboxDocument.InsertText(startOffset, createdText);
    
    textHashesToOffset[request.CalculateTextHash()] = new TextRange(startOffset, endOffset);
    
    return new SandboxCodeFragmentInfo(sandboxPsiSourceFile, startOffset, endOffset);
  }
  
  private static SandboxInfo CreateSandboxInfo(CodeHighlightingRequest request, RdDocumentId rdDocumentId)
  {
    string documentText = request.Text;
    return new SandboxInfo(
      rdDocumentId,
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
    foreach (SandboxFileInfo value in values)
    {
      value.LifetimeDefinition.Terminate();
    }
  }

  protected override string CreateId(IDocument document, SandboxFileInfo value)
  {
    return document.Moniker;
  }
}