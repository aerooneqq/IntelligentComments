using System.Collections.Generic;
using System.Linq;
using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ReSharper.Feature.Services.Daemon.Attributes;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CodeStyle;
using JetBrains.ReSharper.Psi.CSharp.Tree;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Calculations.Builder;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Impl.Content;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public class CommentsProcessor : IRecursiveElementProcessor
{
  [NotNull] [ItemNotNull] private readonly IList<ICommentBase> myComments;
  [NotNull] [ItemNotNull] private readonly IList<ITreeNode> myVisitedComments;

  [NotNull] [ItemNotNull] public IReadOnlyList<ICommentBase> Comments => myComments.AsIReadOnlyList();


  public CommentsProcessor()
  {
    myComments = new List<ICommentBase>();
    myVisitedComments = new List<ITreeNode>();
  }


  public bool InteriorShouldBeProcessed(ITreeNode element) => true;

  public void ProcessBeforeInterior(ITreeNode element)
  {
    if (myVisitedComments.Contains(element)) return;

    switch (element)
    {
      case IDocCommentBlock docCommentBlock:
      {
        var builder = new DocCommentBuilder(docCommentBlock);

        if (builder.Build() is { } comment)
        {
          myComments.Add(comment);
        }

        myVisitedComments.Add(docCommentBlock);
        break;
      }
      case ICSharpCommentNode { CommentType: CommentType.END_OF_LINE_COMMENT } commentNode:
      {
        var builder = new GroupOfLineCommentsBuilder(commentNode);

        if (builder.Build() is { } buildResult)
        {
          myComments.Add(buildResult.GroupOfLineComments);
          myVisitedComments.AddRange(buildResult.CommentNodes);
        }
        
        break;
      }
    }
  }

  public void ProcessAfterInterior(ITreeNode element)
  {
  }

  public bool ProcessingIsFinished => false;
}