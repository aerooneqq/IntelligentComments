using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.RdBackend.Common.Features.Documents;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting;

public interface ICodeHighlightingRequestBuilder
{
  [CanBeNull] ITreeNode CreateNodeFromText([NotNull] string rawCodeText, [NotNull] ITreeNode commentOwner);
  
  [CanBeNull] 
  CodeHighlightingRequest CreateRequest([NotNull] IFile file, [NotNull] ITreeNode commentOwner, [NotNull] ITreeNode code);
}

public interface ICodeHighlightingRequestBuilder<in TFile> : ICodeHighlightingRequestBuilder where TFile : IFile
{
  [CanBeNull] 
  CodeHighlightingRequest CreateRequest([NotNull] TFile file, [NotNull] ITreeNode commentOwner, [NotNull] ITreeNode code);
}

[Language(typeof(CSharpLanguage))]
public class CSharpCodeHighlightingRequestBuilder : ICodeHighlightingRequestBuilder<ICSharpFile>
{
  public ITreeNode CreateNodeFromText(string rawCodeText, ITreeNode commentOwner)
  {
    return CSharpElementFactory.GetInstance(commentOwner).CreateBlock("{" + rawCodeText + "}");
  }
  
  public CodeHighlightingRequest CreateRequest(ICSharpFile file, ITreeNode commentOwner, ITreeNode code)
  {
    if (commentOwner.GetSourceFile()?.Document.GetData(DocumentHostBase.DocumentIdKey) is not { } documentId)
    {
      return null;
    }
    
    var imports = new List<string>();
    foreach (var import in file.Imports)
    {
      imports.Add(import.GetText());
    }

    if ((commentOwner as ICSharpTreeNode)?.GetContainingNamespaceDeclaration() is { DeclaredElement.QualifiedName: { } name })
    {
      return new CSharpCodeHighlightingRequest(file.Language, code.GetText(), documentId, imports, name);
    }

    return null;
  }

  public CodeHighlightingRequest CreateRequest(IFile file, ITreeNode commentOwner, ITreeNode code)
  {
    return file is not ICSharpFile cSharpFile ? null : CreateRequest(cSharpFile, commentOwner, code);
  }
}