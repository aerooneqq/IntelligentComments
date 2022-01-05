using System.Collections.Generic;
using JetBrains.Annotations;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;
using JetBrains.Util;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public class XmlDocsProcessor : IRecursiveElementProcessor
{
  [NotNull] [ItemNotNull] private readonly IList<ICommentBase> myComments;

  [NotNull] [ItemNotNull] public IReadOnlyList<ICommentBase> Comments => myComments.AsIReadOnlyList();


  public XmlDocsProcessor()
  {
    myComments = new List<ICommentBase>();
  }


  public bool InteriorShouldBeProcessed(ITreeNode element) => true;

  public void ProcessBeforeInterior(ITreeNode element)
  {
    if (element is IDocCommentBlock xmlDocOwner)
    {
      var builder = new DocCommentBuilder(xmlDocOwner);

      if (builder.Build() is { } comment)
      {
        myComments.Add(comment);
      }
    }
  }

  public void ProcessAfterInterior(ITreeNode element)
  {
  }

  public bool ProcessingIsFinished => false;
}