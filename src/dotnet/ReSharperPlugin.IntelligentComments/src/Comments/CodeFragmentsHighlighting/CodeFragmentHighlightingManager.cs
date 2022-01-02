using System.Collections.Generic;
using System.Threading;
using JetBrains.Annotations;
using JetBrains.Application.Threading;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.Rider.Model;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.CodeFragmentsHighlighting;

[SolutionComponent]
public class CodeFragmentHighlightingManager
{
  private volatile int myCurrentId;

  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly ISandboxDocumentsHelper myHelper;
  [NotNull] private readonly IShellLocks myShellLocks;
  [NotNull] private readonly object mySyncObject = new();
  [NotNull] private readonly IDictionary<int, CodeHighlightingRequest> myRequests;
  [NotNull] private readonly IDictionary<int, IHighlightedText> myCachedHighlightedCode;


  public CodeFragmentHighlightingManager(
    [NotNull] ILogger logger, 
    [NotNull] ISandboxDocumentsHelper helper,
    [NotNull] IShellLocks shellLocks)
  {
    myLogger = logger;
    myHelper = helper;
    myShellLocks = shellLocks;
    myRequests = new Dictionary<int, CodeHighlightingRequest>();
    myCachedHighlightedCode = new Dictionary<int, IHighlightedText>();
  }


  public int AddRequestForHighlighting(
    string codeText, 
    RdDocumentId documentId,
    IEnumerable<string> imports)
  {
    lock (mySyncObject)
    {
      var nextId = GetNextId();
      myRequests[nextId] = new CodeHighlightingRequest(codeText, documentId, imports);
      return nextId;
    }
  }

  [CanBeNull]
  public IProjectFile TryServeRequest(int id)
  {
    myShellLocks.AssertMainThread();
    lock (mySyncObject)
    {
      if (!myRequests.TryGetValue(id, out var request))
      {
        myLogger.LogAssertion($"Failed to get highlighting request for {id}");
        return null;
      }

      var documentText = request.CreateDocumentText();
      var sandBoxInfo = new SandboxInfo(
        request.DocumentId,
        documentText,
        null,
        true,
        null,
        new List<string>(),
        true,
        new List<CompletionItemType>()
        {
          CompletionItemType.Default,
          CompletionItemType.NamedParameter,
          CompletionItemType.PostfixTemplate,
          CompletionItemType.TemplateItem
        },
        new RdLanguage("C#"),
        true,
        null,
        true,
        new List<char>()
      );

      var sandboxFile = myHelper.GetOrCreateSandboxProjectFile(request.DocumentId, sandBoxInfo, Lifetime.Eternal);
      return sandboxFile;
    }
  }

  private int GetNextId()
  {
    return Interlocked.Increment(ref myCurrentId);
  }
}