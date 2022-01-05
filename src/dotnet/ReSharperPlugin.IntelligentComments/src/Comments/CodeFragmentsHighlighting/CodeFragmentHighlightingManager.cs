using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.Rd.Tasks;
using JetBrains.RdBackend.Common.Features;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Psi.Files.SandboxFiles;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.Rider.Model;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Caches;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;
using ReSharperPlugin.IntelligentComments.Comments.Domain;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;

namespace ReSharperPlugin.IntelligentComments.Comments.CodeFragmentsHighlighting;

[SolutionComponent]
public class CodeFragmentHighlightingManager
{
  [NotNull] private readonly object mySyncObject = new();
  
  private readonly Lifetime myLifetime;
  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly SandboxesCache mySandboxesCache;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly IDictionary<int, CodeHighlightingRequest> myRequests;
  [NotNull] private readonly DocumentHostBase myDocumentHostBase;

  
  private volatile int myCurrentId;


  public CodeFragmentHighlightingManager(
    Lifetime lifetime,
    [NotNull] ISolution solution,
    [NotNull] ILogger logger, 
    [NotNull] SandboxesCache sandboxesCache,
    [NotNull] IShellLocks shellLocks)
  {
    myLifetime = lifetime;
    myLogger = logger;
    mySandboxesCache = sandboxesCache;
    myShellLocks = shellLocks;
    myRequests = new Dictionary<int, CodeHighlightingRequest>();
    
    myDocumentHostBase = DocumentHostBase.GetInstance(solution);
    
    var rdCommentsModel = solution.GetSolution().GetProtocolSolution().GetRdCommentsModel();
    rdCommentsModel.HighlightCode.Set(ServeCodeHighlightingRequest);
  }

  
  private RdTask<RdHighlightedText> ServeCodeHighlightingRequest(Lifetime lifetime, RdCodeHighlightingRequest rdRequest)
  {
    myShellLocks.AssertMainThread();
    lock (mySyncObject)
    {
      var id = rdRequest.Id;

      if (!myRequests.TryGetValue(id, out var request) ||
          myDocumentHostBase.TryGetHostDocument(request.DocumentId) is not { } originalDocument)
      {
        myLogger.Error($"Failed to get highlighting request for {id}");
        return RdTask<RdHighlightedText>.Successful(null);
      }

      var task = new RdTask<RdHighlightedText>();

      void LogErrorAndSetNull(string message)
      {
        myLogger.Error(message);
        task.Set((RdHighlightedText)null);
        RemoveRequest(id);
      }
      
      myShellLocks.QueueReadLock($"{nameof(CodeFragmentHighlightingManager)}::HighlightingCode", () =>
      {
        if (TryCreateSandboxSourceFile(id) is not SandboxPsiSourceFile sourceFile)
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

            var candidate = file.Descendants<IBlock>().Collect().LastOrDefault();
            if (candidate is not { } block)
            {
              LogErrorAndSetNull($"Failed to find block for file with text: {file.GetText()}");
              return;
            }

            var codeHighlighter = LanguageManager.Instance.GetService<IFullCodeHighlighter>(file.Language);
            
            var text = HighlightedText.CreateEmptyText();
            var additionalData = new UserDataHolder();
            additionalData.PutData(CodeHighlightingKeys.SandboxDocumentId, sourceFile.ProjectFile.GetPersistentID());
            additionalData.PutData(CodeHighlightingKeys.OriginalDocument, originalDocument);
            var context = new CodeHighlightingContext(text, additionalData);
            
            block.ProcessThisAndDescendants(codeHighlighter, context);
            
            task.Set(text.ToRdHighlightedText());
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
  
  private IPsiSourceFile TryCreateSandboxSourceFile(int id)
  {
    myShellLocks.AssertMainThread();
    lock (mySyncObject)
    {
      if (!myRequests.TryGetValue(id, out var request) ||
          myDocumentHostBase.TryGetHostDocument(request.DocumentId) is not { } originalDocument)
      {
        myLogger.LogAssertion($"Failed to get highlighting request for {id}");
        return null;
      }

      return mySandboxesCache.CreateSandboxFileFor(originalDocument, request);
    }
  }
  
  private int GetNextId()
  {
    return Interlocked.Increment(ref myCurrentId);
  }
}