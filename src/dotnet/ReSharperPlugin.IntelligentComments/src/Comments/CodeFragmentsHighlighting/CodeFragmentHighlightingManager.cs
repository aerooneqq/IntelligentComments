using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.DocumentManagers;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.Rd.Tasks;
using JetBrains.RdBackend.Common.Features;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.RdBackend.Common.Features.Languages;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.Rider.Model;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting.CSharp;
using ReSharperPlugin.IntelligentComments.Comments.Domain;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.CodeFragmentsHighlighting;

[SolutionComponent]
public class CodeFragmentHighlightingManager
{
  private volatile int myCurrentId;

  private readonly Lifetime myLifetime;
  [NotNull] private readonly ISolution mySolution;
  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly ISandboxDocumentsHelper myHelper;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly IHighlightersProvider myHighlighterProvider;
  [NotNull] private readonly object mySyncObject = new();
  [NotNull] private readonly IDictionary<int, CodeHighlightingRequest> myRequests;
  [NotNull] private readonly IDictionary<int, IHighlightedText> myCachedHighlightedCode;


  public CodeFragmentHighlightingManager(
    Lifetime lifetime,
    [NotNull] ISolution solution,
    [NotNull] ILogger logger, 
    [NotNull] ISandboxDocumentsHelper helper,
    [NotNull] IShellLocks shellLocks,
    [NotNull] IHighlightersProvider highlighterProvider)
  {
    myLifetime = lifetime;
    mySolution = solution;
    myLogger = logger;
    myHelper = helper;
    myShellLocks = shellLocks;
    myHighlighterProvider = highlighterProvider;
    myRequests = new Dictionary<int, CodeHighlightingRequest>();
    myCachedHighlightedCode = new Dictionary<int, IHighlightedText>();
    var rdCommentsModel = solution.GetSolution().GetProtocolSolution().GetRdCommentsModel();
    rdCommentsModel.HighlightCode.Set(ServeCodeHighlightingRequest);
  }

  
  private RdTask<RdHighlightedText> ServeCodeHighlightingRequest(Lifetime lifetime, RdCodeHighlightingRequest rdRequest)
  {
    myShellLocks.AssertMainThread();
    lock (mySyncObject)
    {
      var (id, codeHash, canUseCachedValue) = rdRequest;
      if (canUseCachedValue)
      {
        if (myCachedHighlightedCode.TryGetValue(codeHash, out var highlightedText))
        {
          return RdTask<RdHighlightedText>.Successful(highlightedText.ToRdHighlightedText());
        }
      }
      
      if (!myRequests.ContainsKey(id))
      {
        myLogger.Error($"Failed to get highlighting request for {id}");
        return RdTask<RdHighlightedText>.Successful(null);
      }

      var task = new RdTask<RdHighlightedText>();
      var highlightingLifetimeDef = myLifetime.CreateNested();
      
      void LogErrorAndSetNull(string message)
      {
        myLogger.Error(message);
        task.Set((RdHighlightedText)null);
        RemoveRequest(id);
        highlightingLifetimeDef.Terminate();
      }
      
      myShellLocks.QueueReadLock($"{nameof(CodeFragmentHighlightingManager)}::HighlightingCode", () =>
      {
        if (TryCreateSandboxSourceFile(id, highlightingLifetimeDef.Lifetime) is not { } sourceFile)
        {
          LogErrorAndSetNull($"Failed to create sandbox for {id}");
          return;
        }
        
        myShellLocks.Tasks.Start(new Task(() =>
        {
          using (ReadLockCookie.Create())
          {
            if (sourceFile.GetPrimaryPsiFile() is not { } file)
            {
              LogErrorAndSetNull($"Primary PSI file was null for {sourceFile}");
              return;
            }

            var candidate = file.Descendants<IBlock>().Collect().FirstOrDefault();
            if (candidate is not { } block)
            {
              LogErrorAndSetNull($"Failed to find block for file with text: {file.GetText()}");
              return;
            }

            var idProvider = LanguageManager.Instance.GetService<IHighlightingAttributeIdProvider>(file.Language);
            var highlighter = new CSharpCodeFragmentHighlighter(idProvider, myHighlighterProvider, block);
            
            block.ProcessThisAndDescendants(highlighter);
          
            var newCodeHash = Hash.Create(block.GetText()).Value;
            myCachedHighlightedCode[newCodeHash] = highlighter.Text;
          
            task.Set(highlighter.Text.ToRdHighlightedText());
            highlightingLifetimeDef.Terminate();
            RemoveRequest(id);
          }
        }));
      });

      return task;
    }
  }
  
  private void RemoveRequest(int id)
  {
    lock (mySyncObject)
    {
      myRequests.Remove(id);
    }
  }

  public int AddRequestForHighlighting(CodeHighlightingRequest request)
  {
    lock (mySyncObject)
    {
      var nextId = GetNextId();
      if (nextId == 0)
      {
        nextId = GetNextId();
      }

      myRequests[nextId] = request;
      return nextId;
    }
  }
  
  private IPsiSourceFile TryCreateSandboxSourceFile(int id, Lifetime highlightingLifetime)
  {
    myShellLocks.AssertMainThread();
    lock (mySyncObject)
    {
      if (!myRequests.TryGetValue(id, out var request))
      {
        myLogger.LogAssertion($"Failed to get highlighting request for {id}");
        return null;
      }

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
        //ToDo: WTF?????
        Lifetime.Eternal, 
        riderDocument,
        sandboxFile,
        DocumentHostBase.GetInstance(sandboxFile.GetSolution()));

      return riderDocument.GetPsiSourceFile(mySolution);
    }
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

  private int GetNextId()
  {
    return Interlocked.Increment(ref myCurrentId);
  }
}