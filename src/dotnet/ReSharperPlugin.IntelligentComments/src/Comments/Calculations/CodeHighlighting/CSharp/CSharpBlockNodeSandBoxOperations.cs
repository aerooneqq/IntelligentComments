using System.Collections.Generic;
using System.Linq;
using System.Text;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.CodeHighlighting.CSharp;

public record CSharpCodeHighlightingContext(
  [NotNull] IEnumerable<string> Imports,
  [NotNull] string Namespace
);

public abstract class CSharpBlockNodeSandBoxOperationsBase : ISandBoxTreeNodeOperations
{
  [NotNull] private readonly string myRawText;
  [NotNull] private readonly CSharpCodeHighlightingContext myContext;
  private int myCodeOffsetWithinNamespace;


  protected CSharpBlockNodeSandBoxOperationsBase([NotNull] string rawText, [NotNull] CSharpCodeHighlightingContext context)
  {
    myRawText = rawText;
    myContext = context;
  }
  
  
  public string CreateTextForSandBox()
  {
    (IEnumerable<string> imports, string @namespace) = myContext;
    var sb = new StringBuilder();

    sb.Append("namespace ").Append(@namespace).Append("{\n");

    foreach (string import in imports)
    {
      sb.Append(import).Append("\n");
    }

    myCodeOffsetWithinNamespace = sb.Length;
    
    FillContent(myRawText, sb);
    
    sb.Append("}");

    return sb.ToString();
  }

  protected abstract void FillContent([NotNull] string text, [NotNull] StringBuilder sb);

  public virtual ITreeNode TryFind(IFile file, TreeTextRange range)
  {
    var startOffset = new TreeOffset(range.StartOffset.Offset + myCodeOffsetWithinNamespace);
    var endOffset = new TreeOffset(startOffset.Offset + myRawText.Length);
    return file.FindNodeAt(new TreeTextRange(startOffset, endOffset));
  }
}

public class CSharpBlockNodeSandBoxOperations : CSharpBlockNodeSandBoxOperationsBase
{
  public CSharpBlockNodeSandBoxOperations(
    [NotNull] string rawText, [NotNull] CSharpCodeHighlightingContext context) : base(rawText, context)
  {
  }

  protected override void FillContent(string text, StringBuilder sb)
  {
    sb.Append("\n").Append("class ").Append("ASDASD { \n");
    sb.Append("public static void Foooooooooooo() { \n");

    sb.Append(text);

    sb.Append("}}");
  }

  public override ITreeNode TryFind(IFile file, TreeTextRange range)
  {
    return file.FindNodeAt(range).Descendants<IBlock>().Collect().LastOrDefault();
  }
}

public class CSharpTypeDeclarationNodeOperations : CSharpBlockNodeSandBoxOperationsBase
{
  public CSharpTypeDeclarationNodeOperations(
    [NotNull] string rawText, [NotNull] CSharpCodeHighlightingContext context) : base(rawText, context)
  {
  }
  
  
  protected override void FillContent(string text, StringBuilder sb)
  {
    sb.Append(text);
  }
}