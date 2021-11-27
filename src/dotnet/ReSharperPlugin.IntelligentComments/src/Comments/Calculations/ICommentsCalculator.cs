using System.Collections.Generic;
using JetBrains.DocumentModel;
using ReSharperPlugin.IntelligentComments.Comments.Domain.Core;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations;

public interface ICommentsCalculator
{
  IEnumerable<ICommentBase> CalculateFor(IDocument document);
}