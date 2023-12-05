using System.Text;
using IntelligentComments.Comments.Completion;
using JetBrains.Annotations;
using JetBrains.Application.CommandProcessing;
using JetBrains.Application.Components;
using JetBrains.DocumentModel;
using JetBrains.DocumentModel.Transactions;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.CodeCompletion;
using JetBrains.ReSharper.Feature.Services.CSharp.TypingAssist;
using JetBrains.ReSharper.Feature.Services.TypingAssist;
using JetBrains.ReSharper.Features.ReSpeller.Analyzers;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp.Parsing;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Parsing;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.ReSharper.Psi.Xml.Tree;
using JetBrains.TextControl;
using JetBrains.Util;

namespace IntelligentComments.Comments.TypingAssists;


[SolutionComponent]
public class DocCommentTypingAssist : CSharpTypingAssistBase, ITypingHandler
{
  private record Context(
    int CaretOffset, 
    [NotNull] ITreeNode CSharpToken, 
    [NotNull] ITreeNode DocCommentToken
  );


  public DocCommentTypingAssist(
    Lifetime lifetime, 
    TypingAssistDependencies dependencies,
    DocumentTransactionManager transactionManager,
    [NotNull] IOptional<ICodeCompletionSessionManager> sessionManager) 
    : base(lifetime, dependencies, transactionManager, sessionManager)
  {
    dependencies.TypingAssistManager.AddTypingHandler(lifetime, '"', this, HandleQuoteInDocComment);
    dependencies.TypingAssistManager.AddActionHandler(lifetime, "TextControl.Enter", this, HandleEnterInDocComment);
  }

  
  private bool HandleQuoteInDocComment([NotNull] ITypingContext context)
  {
    if (TryGetContext(context) is not { } docCommentContext) return false;

    var (caretOffset, _, docCommentToken) = docCommentContext;
    var xmlTokenTypes = XmlTokenTypes.GetInstance(docCommentToken.Language);
    var editor = context.TextControl;
    
    if (docCommentToken.NodeType == xmlTokenTypes.EQ &&
        docCommentToken.Parent is IXmlAttribute &&
        docCommentToken.GetPreviousToken()?.GetTokenType() == xmlTokenTypes.IDENTIFIER)
    {
      using (CommandProcessor.UsingCommand($"{GetType().Name}::{nameof(HandleQuoteInDocComment)}"))
      {
        if (docCommentToken.GetNextToken()?.GetText().StartsWith("\"") ?? true)
        {
          editor.Document.InsertText(caretOffset, "\"");
          return true;
        }

        editor.Document.InsertText(caretOffset, "\"\"");
        editor.Caret.MoveTo(caretOffset + 1, CaretVisualPlacement.Generic);
        return true; 
      }
    }
    
    return false;
  }
  
  [CanBeNull] private Context TryGetContext([NotNull] ITypingContext context) => TryGetContext(context.TextControl);
  [CanBeNull] private Context TryGetContext([NotNull] IActionContext context) => TryGetContext(context.TextControl);

  [CanBeNull]
  private Context TryGetContext([NotNull] ITextControl editor)
  {
    if (CommitPsi(editor) is not ICSharpFile cSharpFile) return null;
    if (LanguageManager.Instance.TryGetService<IPsiHelper>(cSharpFile.Language) is not { } helper) return null;
    
    var caretOffset = (int)editor.Caret.DocOffset();
    var documentOffset = new DocumentOffset(editor.Document, caretOffset);
    
    if (cSharpFile.FindTokenAt(new TreeOffset(documentOffset.Offset)) is not { } token) return null;
    if (token.TryFindDocCommentBlock() is not { } docCommentBlock) return null;
    if (helper.GetXmlDocPsi(docCommentBlock) is not { XmlFile: { } xmlFile }) return null;
    if (xmlFile.FindNodeAt(xmlFile.Translate(documentOffset)) is not { } docCommentToken) return null;

    return new Context(caretOffset, token, docCommentToken);
  }
  
  private bool HandleEnterInDocComment([NotNull] IActionContext context)
  {
    if (TryGetContext(context) is not { } docCommentContext) return false;
    
    var editor = context.TextControl;
    var (_, token, docCommentToken) = docCommentContext;

    return HandleEnterOnSpace(editor, docCommentToken, token);
  }

  [NotNull] 
  private static string GetTextToInsertOnEnter(string docCommentIndent) => $"\n{docCommentIndent}/// \n{docCommentIndent}/// ";

  private bool HandleEnterOnSpace(
    [NotNull] ITextControl editor, [NotNull] ITreeNode docCommentToken, [NotNull] ITreeNode cSharpToken)
  {
    var xmlTokenTypes = XmlTokenTypes.GetInstance(docCommentToken.Language);
    var prevNonWhiteSpaceToken = GetNonWhiteSpaceToken(docCommentToken, false);
    var nextNonWhiteSpaceToken = docCommentToken.NodeType == xmlTokenTypes.TAG_END1 
      ? docCommentToken
      : GetNonWhiteSpaceToken(docCommentToken.GetNextToken(), true);

    if (prevNonWhiteSpaceToken?.NodeType != xmlTokenTypes.TAG_END ||
        nextNonWhiteSpaceToken?.NodeType != xmlTokenTypes.TAG_START1 ||
        prevNonWhiteSpaceToken?.Parent is not IXmlTagHeader { Name.XmlName: { } headerName } ||
        nextNonWhiteSpaceToken?.Parent is not IXmlTagFooter { Name.XmlName: { } footerName } ||
        headerName != footerName)
    {
      return false;
    }

    using (CommandProcessor.UsingCommand($"{GetType().Name}::{nameof(HandleEnterOnSpace)}"))
    {
      var spacesStartOffset = prevNonWhiteSpaceToken.GetDocumentEndOffset();
      var spacesEndOffset = nextNonWhiteSpaceToken.GetDocumentStartOffset();
      var indent = GetDocCommentIndent(cSharpToken);
      editor.Document.ReplaceText(new DocumentRange(spacesStartOffset, spacesEndOffset), GetTextToInsertOnEnter(indent));

      var caretOffset = prevNonWhiteSpaceToken.GetDocumentEndOffset().Offset + 1 + indent.Length + 3 + 1;
      editor.Caret.MoveTo(caretOffset, CaretVisualPlacement.DontScrollIfVisible);
      return true; 
    }
  }

  [NotNull]
  private static string GetDocCommentIndent([CanBeNull] ITreeNode node)
  {
    while (node is { } && node.NodeType != CSharpTokenType.END_OF_LINE_COMMENT)
      node = node.PrevSibling;

    node = node?.PrevSibling;
    var sb = new StringBuilder();
    
    while (node is { } && node.NodeType != CSharpTokenType.NEW_LINE)
    {
      sb.Prepend(node.GetText());
      node = node.PrevSibling;
    }
    
    return sb.ToString();
  }

  [CanBeNull]
  private static ITreeNode GetNonWhiteSpaceToken([CanBeNull] ITreeNode node, bool rightDirection)
  {
    [CanBeNull]
    ITreeNode TryGetNextNode(ITreeNode treeNode) => rightDirection switch
    {
      true => treeNode.GetNextToken(),
      false => treeNode.GetPreviousToken()
    };

    if (node is null) return null;
    if (!node.IsWhitespaceToken()) return node;
    
    node = TryGetNextNode(node);
    while (node is { } && node.IsWhitespaceToken())
      node = TryGetNextNode(node);

    return node;
  }

  protected override bool IsSupported(ITextControl textControl)
  {
    return true;
  }

  public override bool QuickCheckAvailability(ITextControl textControl, IPsiSourceFile projectFile)
  {
    return true;
  }
}