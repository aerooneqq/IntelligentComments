using System.Collections.Generic;
using System.Threading;
using IntelligentComments.Comments.Calculations.CodeHighlighting;
using IntelligentComments.Comments.Calculations.Core.DocComments;
using IntelligentComments.Comments.Domain.Impl;
using IntelligentComments.Rider.Comments.Caches.Sandboxes;
using IntelligentComments.Rider.Comments.Domain;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.Collections.Viewable;
using JetBrains.DataFlow;
using JetBrains.ProjectModel;
using JetBrains.Rd.Tasks;
using JetBrains.RdBackend.Common.Features;
using JetBrains.RdBackend.Common.Features.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Project;
using JetBrains.ReSharper.Feature.Services.Protocol;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Files;
using JetBrains.ReSharper.Resources.Shell;
using JetBrains.Rider.Backend.Features.Preview.WPF;
using JetBrains.Rider.Backend.Features.ProjectModel;
using JetBrains.Rider.Model;
using JetBrains.Util;

namespace IntelligentComments.Rider.Comments.CodeFragmentsHighlighting;

[SolutionComponent]
public class CodeFragmentHighlightingManager : ICodeFragmentHighlightingManager
{
  [NotNull] private readonly object mySyncObject = new();

  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly SandboxesCache mySandboxesCache;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly IPsiServices myPsiServices;
  [NotNull] private readonly IDictionary<int, CodeHighlightingRequest> myRequests;

  
  private volatile int myCurrentId;


  public CodeFragmentHighlightingManager(
    [NotNull] ISolution solution,
    [NotNull] ILogger logger,
    [NotNull] SandboxesCache sandboxesCache,
    [NotNull] IShellLocks shellLocks,
    [NotNull] IPsiServices psiServices,
    [NotNull] FeaturesStartupMonitor solutionLoadStateMonitor)
  {
    myLogger = logger;
    mySandboxesCache = sandboxesCache;
    myShellLocks = shellLocks;
    myPsiServices = psiServices;
    myRequests = new Dictionary<int, CodeHighlightingRequest>();
    
    var rdCommentsModel = solution.GetSolution().GetProtocolSolution().GetRdCommentsModel();
    rdCommentsModel.HighlightCode.SetAsync((lt, request) =>
    {
      var task = new RdTask<RdHighlightedText>();
      solutionLoadStateMonitor.FullStartupFinished.WhenTrue(lt, _ =>
      {
        myShellLocks.QueueReadLock(lt, $"{nameof(CodeFragmentHighlightingManager)}::ServingRequest", () =>
        {
          ServeCodeHighlightingRequest(task, request);
        });
      });

      return task;
    });
  }

  
  private void ServeCodeHighlightingRequest(
    [NotNull] RdTask<RdHighlightedText> task, 
    [NotNull] RdCodeHighlightingRequest rdRequest)
  {
    myShellLocks.AssertMainThread();
    var id = rdRequest.Id;

    void LogWarnAndSetNull(string message)
    {
      myLogger.Warn(message);
      task.Set((RdHighlightedText) null);
      RemoveRequest(id);
    }
    
    if (TryGetRequest(id) is not { } request)
    {
      LogWarnAndSetNull($"Failed to get request for id: {id}");
      return;
    }

    if (TryCreateSandboxSourceFile(request) is not var (sourceFile, startOffset, endOffset))
    {
      LogWarnAndSetNull($"Failed to create sandbox for {id}");
      return;
    }

    myPsiServices.Files.ExecuteAfterCommitAllDocuments(() =>
    {
      using (ReadLockCookie.Create())
      {
        if (sourceFile.GetPrimaryPsiFile() is not { } file)
        {
          LogWarnAndSetNull($"Primary PSI file was null for {sourceFile}");
          return;
        }

        var range = new TreeTextRange(new TreeOffset(startOffset), new TreeOffset(endOffset));
        if (request.Operations.TryFind(file, range) is not { } candidate)
        {
          LogWarnAndSetNull($"Failed to find block for file with text: {file.GetText()}");
          return;
        }

        var codeHighlighter = LanguageManager.Instance.GetService<IFullCodeHighlighter>(file.Language);

        var text = HighlightedText.CreateEmptyText();
        var additionalData = new UserDataHolder();
        additionalData.PutData(CodeHighlightingKeys.SandboxDocumentId, sourceFile.ToProjectFile()!.GetPersistentID());
        additionalData.PutData(CodeHighlightingKeys.OriginalDocument, request.Document);
        var context = new CodeHighlightingContext(text, additionalData);

        candidate.ProcessThisAndDescendants(codeHighlighter, context);

        task.Set(text.ToRdHighlightedText());
        RemoveRequest(id);
      }
    });
  }
  
  private CodeHighlightingRequest TryGetRequest(int id)
  {
    lock (mySyncObject)
    {
      if (!myRequests.TryGetValue(id, out var request))
      {
        myLogger.Warn($"Failed to get highlighting request for {id}");
        return null;
      }

      return request;
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

  private SandboxCodeFragmentInfo TryCreateSandboxSourceFile(CodeHighlightingRequest request)
  {
    myShellLocks.AssertMainThread();
    return mySandboxesCache.GetOrCreateSandboxFileForHighlighting(request);
  }

  private int GetNextId()
  {
    return Interlocked.Increment(ref myCurrentId);
  }
}