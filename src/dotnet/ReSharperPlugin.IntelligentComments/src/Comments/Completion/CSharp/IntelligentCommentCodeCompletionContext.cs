using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion.Infrastructure;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.Psi.Pointers;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Completion.CSharp;

public class IntelligentCommentCodeCompletionContext : ISpecificCodeCompletionContext
{
  public string ContextId => nameof(IntelligentCommentCodeCompletionContext);
  public PsiLanguageType Language => CSharpLanguage.Instance;
  
  public CodeCompletionContext BasicContext { get; }
  [NotNull] public ITokenNode ContextToken { get; }
  [NotNull] public TextLookupRanges TextLookupRanges { get; }


  public IntelligentCommentCodeCompletionContext(
    [NotNull] CodeCompletionContext basicContext, 
    [NotNull] ITokenNode contextToken,
    [NotNull] TextLookupRanges textLookupRanges)
  {
    BasicContext = basicContext;
    ContextToken = contextToken;
    TextLookupRanges = textLookupRanges;
  }
  
  
  public IElementInstancePointer<IDeclaredElement> CreateElementPointer(
    DeclaredElementInstance<IDeclaredElement> instance) => new TrivialElementInstancePointer<IDeclaredElement>(instance);

  public DeclaredElementsOrPointers CreatePointer(IDeclaredElement declaredElement) => new(declaredElement);
  public DeclaredElementsOrPointers CreatePointer(DeclaredElementInstance instance) => new(instance);
}

public static class InvariantCodeCompletionContextExtensions
{
  [CanBeNull]
  public static IXmlAttribute TryGetContextAttribute([NotNull] this IntelligentCommentCodeCompletionContext context)
  {
    return context.ContextToken.Parent as IXmlAttribute;
  }

  [NotNull] 
  public static ISolution GetSolution([NotNull] this IntelligentCommentCodeCompletionContext context)
  {
    return context.ContextToken.GetSolution();
  }
}