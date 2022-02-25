using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Pointers;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;

public class IntelligentCommentCompletionContext : ISpecificCodeCompletionContext
{
  public string ContextId => nameof(IntelligentCommentCompletionContext);
  public PsiLanguageType Language => CSharpLanguage.Instance;
  
  public CodeCompletionContext BasicContext { get; }
  [NotNull] public ITokenNode ContextToken { get; }
  [NotNull] public TextLookupRanges TextLookupRanges { get; }
  [NotNull] public IDocCommentBlock DocCommentBlock { get; }


  public IntelligentCommentCompletionContext(
    [NotNull] CodeCompletionContext basicContext, 
    [NotNull] ITokenNode contextToken,
    [NotNull] TextLookupRanges textLookupRanges,
    [NotNull] IDocCommentBlock docCommentBlock)
  {
    BasicContext = basicContext;
    ContextToken = contextToken;
    TextLookupRanges = textLookupRanges;
    DocCommentBlock = docCommentBlock;
  }
  
  
  public IElementInstancePointer<IDeclaredElement> CreateElementPointer(
    DeclaredElementInstance<IDeclaredElement> instance) => new TrivialElementInstancePointer<IDeclaredElement>(instance);

  public DeclaredElementsOrPointers CreatePointer(IDeclaredElement declaredElement) => new(declaredElement);
  public DeclaredElementsOrPointers CreatePointer(DeclaredElementInstance instance) => new(instance);
}

public static class IntelligentCommentCodeCompletionContextExtensions
{
  [CanBeNull]
  public static IXmlAttribute TryGetContextAttribute([NotNull] this IntelligentCommentCompletionContext context)
  {
    return context.ContextToken.Parent as IXmlAttribute;
  }

  [NotNull] 
  public static ISolution GetSolution([NotNull] this IntelligentCommentCompletionContext context)
  {
    return context.ContextToken.GetSolution();
  }

  [CanBeNull]
  public static IDocCommentBlockOwner TryFindDocumentedEntity([NotNull] this IntelligentCommentCompletionContext context)
  {
    return context.DocCommentBlock.Parent as IDocCommentBlockOwner;
  }
}