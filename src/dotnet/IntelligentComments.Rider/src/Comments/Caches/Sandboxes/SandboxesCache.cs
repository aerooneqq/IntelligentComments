using System.Collections.Generic;
using IntelligentComments.Comments.Caches;
using IntelligentComments.Comments.Calculations.CodeHighlighting;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.DocumentManagers;
using JetBrains.DocumentModel;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.RdBackend.Common.Features.Languages;
using JetBrains.ReSharper.Feature.Services.Refactorings;
using JetBrains.ReSharper.Psi;
using JetBrains.Rider.Model;
using JetBrains.TextControl;
using JetBrains.Util;

namespace IntelligentComments.Rider.Comments.Caches.Sandboxes;

public record SandboxFileInfo(
  [NotNull] LifetimeDefinition LifetimeDefinition,
  [NotNull] IPsiSourceFile SandboxPsiSourceFile,
  [NotNull] IDictionary<int, TextRange> TextHashesToOffset
);

public record SandboxCodeFragmentInfo([NotNull] IPsiSourceFile SourceFile, int StartOffset, int EndOffset);

[SolutionComponent]
public class SandboxesCache : AbstractOpenedDocumentBasedCache<string, SandboxFileInfo>, ISandboxesCache
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
  
  
  public IPsiSourceFile TryGetSandboxPsiSourceFile(IDocument originalDocument, string fileName)
  {
    return TryGetValue(originalDocument, fileName)?.SandboxPsiSourceFile;
  }

  public SandboxCodeFragmentInfo GetOrCreateSandboxFileForHighlighting([NotNull] CodeHighlightingRequest request)
  {
    myShellLocks.AssertMainThread();
    
    var lifetimeDef = myLifetime.CreateNested();
    var highlightingLifetime = lifetimeDef.Lifetime;
    var documentId = request.Document.GetProtocolSynchronizer().DocumentId;
    
    var sandBoxInfo = CreateSandboxInfo(request, documentId);
    var sandboxFile = myHelper.GetOrCreateSandboxProjectFile(documentId, sandBoxInfo, highlightingLifetime);

    var originalDocument = request.Document;
    if (TryGetValue(originalDocument, originalDocument.Moniker) is { } existingSandboxFileInfo)
    {
      return AddTextIfNeededAndGetFragment(existingSandboxFileInfo, request);
    }
    
    var document = sandboxFile.GetDocument();
    if (document is not RiderDocument riderDocument)
    {
      myLogger.LogAssertion($"Got unexpected document for {sandboxFile}: {document}");
      return null;
    }

    var model = new RiderTextBufferDocumentModel(new List<DocumentExtension>(), CreationSide.Backend);
    model.BindToLocalProtocol(highlightingLifetime);
    myHelper.InitSandboxDocument(
      documentId,
      model,
      sandBoxInfo,
      highlightingLifetime, 
      riderDocument,
      sandboxFile);

    var sourceFile = riderDocument.GetPsiSourceFile(mySolution);
    if (sourceFile is not { })
    {
      myLogger.LogAssertion($"Got unexpected sourceFile for {sandboxFile}: {sourceFile}");
      return null;
    }

    var endOffset = riderDocument.GetTextLength();
    var idToOffsets = new Dictionary<int, TextRange>
    {
      [request.CalculateTextHash()] = new(0, endOffset)
    };
    
    Add(originalDocument, new SandboxFileInfo(lifetimeDef, sourceFile, idToOffsets));

    return new SandboxCodeFragmentInfo(sourceFile, 0, endOffset);
  }
  
  [NotNull]
  private static SandboxCodeFragmentInfo AddTextIfNeededAndGetFragment(
    [NotNull] SandboxFileInfo sandboxFileInfo,
    [NotNull] CodeHighlightingRequest request)
  {
    var (_, sandboxPsiSourceFile, textHashesToOffset) = sandboxFileInfo;
    if (textHashesToOffset.TryGetValue(request.CalculateTextHash(), out var existingRange))
    {
      return new SandboxCodeFragmentInfo(sandboxPsiSourceFile, existingRange.StartOffset, existingRange.EndOffset);
    }
    
    var sandboxDocument = sandboxPsiSourceFile.Document;
    var startOffset = sandboxDocument.GetTextLength();
    var createdText = request.Text;
    var endOffset = startOffset + createdText.Length;
    
    sandboxDocument.InsertText(startOffset, createdText);
    
    textHashesToOffset[request.CalculateTextHash()] = new TextRange(startOffset, endOffset);
    
    return new SandboxCodeFragmentInfo(sandboxPsiSourceFile, startOffset, endOffset);
  }
  
  private static SandboxInfo CreateSandboxInfo(CodeHighlightingRequest request, RdDocumentId rdDocumentId)
  {
    var documentText = request.Text;
    return new SandboxInfo(
      "IntelligentCommentsSandbox",
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
      false,
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

  protected override string CreateId(IDocument document, SandboxFileInfo value)
  {
    return document.Moniker;
  }
}