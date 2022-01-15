using System;
using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
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
      ReferenceCollection references = element.Parent.GetReferences();
      
      var consumer = new MyHighlightingsConsumer();
      myCSharpIdentifierHighlighter.Highlight(element, consumer, references);
      
      if (consumer.Highlightings.Count > 0)
      {
        IHighlighting highlighting = consumer.Highlightings.First().Highlighting;
        if (highlighting is ICustomAttributeIdHighlighting { AttributeId: { } attributeId })
        {
          string text = element.GetText();
          
          TextHighlighter highlighter;
          if (TryGetReferenceFrom(element, references, context) is { } codeEntityReference)
          {
            var resolveContext = new ResolveContextImpl(element.GetSolution(), element.GetSourceFile()?.Document);
            highlighter = HighlightersProvider.TryGetReSharperHighlighter(text.Length, codeEntityReference, resolveContext);
            
            if (highlighter is { })
            {
              IDocument originalDocument = context.AdditionalData.GetData(CodeHighlightingKeys.OriginalDocument);
              int id = element.GetSolution().GetComponent<ReferencesCache>().AddReference(originalDocument, codeEntityReference);
              highlighter = highlighter with { References = new[] { new ProxyReference(id) } };
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
  private ICodeEntityReference TryGetReferenceFrom(
    [NotNull] ITreeNode node,
    [NotNull] IEnumerable<IReference> references, 
    [NotNull] CodeHighlightingContext context)
  {
    foreach (IReference reference in references)
    {
      try
      {
        IDeclaredElement resolveResult = reference.Resolve().DeclaredElement;
        string sandboxDocId = context.AdditionalData.GetData(CodeHighlightingKeys.SandboxDocumentId);
        IDocument originalDocument = context.AdditionalData.GetData(CodeHighlightingKeys.OriginalDocument);
        TextRange textRange = node.GetDocumentRange().TextRange;

        if (resolveResult is null || sandboxDocId is null || originalDocument is null) return null;
        
        return new SandBoxCodeEntityReference(
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