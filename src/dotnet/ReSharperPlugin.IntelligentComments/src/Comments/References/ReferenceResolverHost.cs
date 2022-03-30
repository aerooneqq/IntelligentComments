using System;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.Rider.Backend.Features.Documents;
using JetBrains.Rider.Model;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations;
using ReSharperPlugin.IntelligentComments.Comments.Domain;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
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
        result.Set(new RdInvalidResolveResult(new RdHighlightedText(message)));
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

      var resolveContext = new DomainResolveContextImpl(solution, document);
      var resolveResult = reference.Resolve(resolveContext);
      
      result.Set(resolveResult.ToRdResolveResult());
      return result;
    });
  }
}

public static class ResolveResultExtensions
{
  [NotNull]
  public static RdResolveResult ToRdResolveResult([NotNull] this DomainResolveResult domainResolveResult)
  {
    if (domainResolveResult is InvalidDomainResolveResult invalidResolveResult)
    {
      var text = invalidResolveResult.Error;
      var highlighter = LanguageManager.Instance.GetService<IHighlightersProvider>(CSharpLanguage.Instance).TryGetDocCommentHighlighter(text.Length);
      var highlightedText = new HighlightedText(text, highlighter);
      return new RdInvalidResolveResult(highlightedText.ToRdHighlightedText());
    }
    
    return domainResolveResult switch
    {
      InvariantDomainResolveResult result => new RdInvariantResolveResult(result.Invariant.ToRdInvariant()),
      EmptyDomainResolveResult => new RdInvalidResolveResult(null),
      _ => throw new ArgumentOutOfRangeException(domainResolveResult.GetType().Name)
    };
  }
}