using System;
using IntelligentComments.Comments.Calculations;
using IntelligentComments.Comments.Domain.Core.References;
using IntelligentComments.Comments.Domain.Impl;
using IntelligentComments.Comments.Domain.Impl.References;
using IntelligentComments.Rider.Comments.Domain;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.Rd.Tasks;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.ReSharper.Feature.Services.Protocol;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.Rider.Model;
using JetBrains.Util;

namespace IntelligentComments.Rider.Comments.RdReferences;

[SolutionComponent]
public class ReferenceResolverHost
{
  public ReferenceResolverHost(
    [NotNull] ILogger logger,
    [NotNull] ISolution solution,
    [NotNull] RdReferenceConverter referenceConverter,
    [NotNull] IDocumentHost documentHost)
  {
    solution.GetProtocolSolution().GetRdCommentsModel().ResolveReference.SetAsync((_, resolveRequest) =>
    {
      var result = new RdTask<RdResolveResult>();
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
      if (documentHost.TryGetDocument(documentId) is not { } document)
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
      NamedEntityDomainResolveResult result => new RdNamedEntityResolveResult(
        result.NameKind.ToRdNameKind(), result.ContentSegment?.ToRdContentSegment()),
      EmptyDomainResolveResult => new RdInvalidResolveResult(null),
      DomainWebResourceResolveResult result => new RdWebResourceResolveResult(result.Link),
      _ => throw new ArgumentOutOfRangeException(domainResolveResult.GetType().Name)
    };
  }
}