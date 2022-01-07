using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting.CSharp;


[Language(typeof(CSharpLanguage))]
public class CSharpCodeHighlightingRequestBuilder : ICodeHighlightingRequestBuilder
{
  public SandBoxNodeCreationResult? CreateNodeOperations(string rawCodeText, ITreeNode commentOwner)
  {
    var factory = CSharpElementFactory.GetInstance(commentOwner);
    if (CreateContext(commentOwner) is not { } context) return null;

    return TryCreateBlock(rawCodeText, factory, context) ??
           TryCreateTypeDeclaration(rawCodeText, factory, context);
  }

  [CanBeNull]
  private static SandBoxNodeCreationResult? TryCreateBlock(
    [NotNull] string rawCodeText,
    [NotNull] CSharpElementFactory factory, 
    [NotNull] CSharpCodeHighlightingContext context)
  {
    var node = factory.CreateBlock("{" + rawCodeText + "}");
    return HasAnyErrorElements(node) ? null : new(node, new CSharpBlockNodeSandBoxOperations(node.GetText(), context));
  }

  private static bool HasAnyErrorElements(ITreeNode node) => node.Descendants<IErrorElement>().Collect().Any();

  private static SandBoxNodeCreationResult? TryCreateTypeDeclaration(
    [NotNull] string rawCodeText,
    [NotNull] CSharpElementFactory factory, 
    [NotNull] CSharpCodeHighlightingContext context)
  {
    var node = factory.CreateFile(rawCodeText);
    return new(node, new CSharpTypeDeclarationNodeOperations(node.GetText(), context));
  }

  private CSharpCodeHighlightingContext CreateContext(ITreeNode commentOwner)
  {
    if (commentOwner.GetContainingFile() is not ICSharpFile file) return null;
    
    var imports = new List<string>();
    foreach (var import in file.Imports)
    {
      imports.Add(import.GetText());
    }

    if ((commentOwner as ICSharpTreeNode)?.GetContainingNamespaceDeclaration() is { DeclaredElement.QualifiedName: { } name })
    {
      return new CSharpCodeHighlightingContext(imports, name);
    }

    return null;
  }
}