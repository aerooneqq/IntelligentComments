using System;
using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features.SyntaxHighlighting.CSharp;
using JetBrains.ReSharper.Daemon.CSharp.Stages;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Parsing;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using JetBrains.Util.Logging;
using ReSharperPlugin.IntelligentComments.Comments.Caches;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.References;
using IReference = JetBrains.ReSharper.Psi.Resolve.IReference;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting.CSharp;

[Language(typeof(CSharpLanguage))]
public class CSharpFullCodeFragmentHighlighter : CodeHighlighterBase, IFullCodeHighlighter
{
  [NotNull] private readonly ILogger myLogger;
  [NotNull] private readonly CSharpIdentifierHighlighter myCSharpIdentifierHighlighter;
  

  public CSharpFullCodeFragmentHighlighter(
    [NotNull] IHighlightingAttributeIdProvider attributeIdProvider,
    [NotNull] IHighlightersProvider highlightersProvider)
    : base(highlightersProvider, new CSharpFullSyntaxHighlightingProcessor())
  {
    myLogger = Logger.GetLogger<CSharpFullCodeFragmentHighlighter>();
    myCSharpIdentifierHighlighter = new CSharpIdentifierHighlighter(attributeIdProvider);
  }


  protected override void ProcessBeforeInteriorInternal(ITreeNode element, CodeHighlightingContext context)
  {
    if (element.NodeType == CSharpTokenType.IDENTIFIER)
    {
      var references = element.Parent.GetReferences();
      
      var consumer = new MyHighlightingsConsumer();
      myCSharpIdentifierHighlighter.Highlight(element, consumer, references);
      
      if (consumer.Highlightings.Count > 0)
      {
        var highlighting = consumer.Highlightings.First().Highlighting;
        if (highlighting is ICustomAttributeIdHighlighting { AttributeId: { } attributeId })
        {
          var text = element.GetText();
          
          TextHighlighter highlighter;
          if (TryGetReferenceFrom(element, references, context) is { } codeEntityReference)
          {
            var resolveContext = new DomainResolveContextImpl(element.GetSolution(), element.GetSourceFile()?.Document);
            highlighter = HighlightersProvider.TryGetReSharperHighlighter(text.Length, codeEntityReference, resolveContext);
            
            if (highlighter is { })
            {
              var originalDocument = context.AdditionalData.GetData(CodeHighlightingKeys.OriginalDocument);
              var cache = element.GetSolution().GetComponent<ReferencesCache>();
              var id = cache.AddReferenceIfNotPresent(originalDocument, codeEntityReference);
              highlighter = highlighter with { References = new[] { new ProxyDomainReference(id, codeEntityReference.RawValue) } };
            }
          }
          else
          {
            highlighter = HighlightersProvider.TryGetReSharperHighlighter(attributeId, text.Length);
          }
          
          context.Text.Add(highlighter is { } ? new HighlightedText(text, highlighter) : new HighlightedText(text));
          return;
        }
      }
    }
    
    context.Text.Add(new HighlightedText(element.GetText()));
  }
  
  [CanBeNull]
  private ICodeEntityDomainReference TryGetReferenceFrom(
    [NotNull] ITreeNode node,
    [NotNull] IEnumerable<IReference> references, 
    [NotNull] CodeHighlightingContext context)
  {
    foreach (var reference in references)
    {
      try
      {
        var resolveResult = reference.Resolve().DeclaredElement;
        var sandboxDocId = context.AdditionalData.GetData(CodeHighlightingKeys.SandboxDocumentId);
        var originalDocument = context.AdditionalData.GetData(CodeHighlightingKeys.OriginalDocument);
        var textRange = node.GetDocumentRange().TextRange;

        if (resolveResult is null || sandboxDocId is null || originalDocument is null) return null;
        
        return new SandBoxCodeEntityDomainReference(
          resolveResult.ShortName, sandboxDocId, originalDocument, textRange, resolveResult);
      }
      catch (Exception ex)
      {
        myLogger.LogException(ex);
        return null;
      }
    }

    return null;
  }
  
  private class MyHighlightingsConsumer : IHighlightingConsumer
  {
    [NotNull] private readonly List<HighlightingInfo> myHighlightingInfos;


    public IReadOnlyList<HighlightingInfo> Highlightings => myHighlightingInfos;

    
    public MyHighlightingsConsumer()
    {
      myHighlightingInfos = new List<HighlightingInfo>();
    }

    
    public void ConsumeHighlighting(HighlightingInfo highlightingInfo)
    {
      myHighlightingInfos.Add(highlightingInfo);
    }
  }
}