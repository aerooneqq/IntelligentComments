using System;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features;
using JetBrains.Rider.Backend.Features.Documents;
using JetBrains.Rider.Model;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;

namespace ReSharperPlugin.IntelligentComments.Comments.References;

[SolutionComponent]
public class ReferenceResolverHost
{
  public ReferenceResolverHost(
    [NotNull] ILogger logger,
    [NotNull] RiderDocumentHost documentHost,
    [NotNull] ISolution solution,
    [NotNull] RdReferenceConverter referenceConverter)
  {
    solution.GetProtocolSolution().GetRdCommentsModel().ResolveReference.Set((_, resolveRequest) =>
    {
      var result = new JetBrains.Rd.Tasks.RdTask<RdResolveResult>();
      void LogErrorAndSetInvalidResolveResult([NotNull] string message)
      {
        logger.Error(message);
        result.Set(new RdInvalidResolveResult());
      }

      if (referenceConverter.TryGetReference(resolveRequest) is not { } reference)
      {
        LogErrorAndSetInvalidResolveResult($"Failed to resolve convert RD reference in request: {resolveRequest}");
        return result;
      }

      var documentId = resolveRequest.TextControlId.DocumentId;
      if (documentHost.TryGetHostDocument(documentId) is not { } document)
      {
        LogErrorAndSetInvalidResolveResult($"Failed to get document for {documentId}");
        return result;
      }

      var resolveContext = new ResolveContextImpl(solution, document);
      var resolveResult = reference.Resolve(resolveContext);
      
      result.Set(resolveResult.ToRdResolveResult());
      return result;
    });
  }
}

public static class ResolveResultExtensions
{
  public static RdResolveResult ToRdResolveResult([NotNull] this ResolveResult resolveResult)
  {
    return resolveResult switch
    {
      InvariantResolveResult result => new RdInvariantResolveResult(result.Invariant.ToRdInvariant()),
      EmptyResolveResult => new RdInvalidResolveResult(),
      _ => throw new ArgumentOutOfRangeException(resolveResult.GetType().Name)
    };
  }
}