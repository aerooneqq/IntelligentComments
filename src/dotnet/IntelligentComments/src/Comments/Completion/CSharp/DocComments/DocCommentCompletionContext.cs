using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;

namespace IntelligentComments.Comments.Completion.CSharp.DocComments;

public class DocCommentCompletionContext : SpecificCodeCompletionContext
{
  public override string ContextId => nameof(DocCommentCompletionContext);
  
  [NotNull] public ITokenNode ContextToken { get; }
  [NotNull] public IDocCommentBlock DocCommentBlock { get; }


  public DocCommentCompletionContext(
    [NotNull] CodeCompletionContext basicContext, 
    [NotNull] ITokenNode contextToken,
    [NotNull] IDocCommentBlock docCommentBlock) 
    : base(basicContext)
  {
    ContextToken = contextToken;
    DocCommentBlock = docCommentBlock;
  }
}

public static class IntelligentCommentCodeCompletionContextExtensions
{
  [CanBeNull]
  public static IXmlAttribute TryGetContextAttribute([NotNull] this DocCommentCompletionContext context)
  {
    return context.ContextToken.Parent as IXmlAttribute;
  }

  [NotNull] 
  public static ISolution GetSolution([NotNull] this DocCommentCompletionContext context)
  {
    return context.ContextToken.GetSolution();
  }

  [CanBeNull]
  public static IDocCommentBlockOwner TryFindDocumentedEntity([NotNull] this DocCommentCompletionContext context)
  {
    return context.DocCommentBlock.Parent as IDocCommentBlockOwner;
  }
}