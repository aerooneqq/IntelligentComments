using System.Collections.Generic;
using JetBrains.Application.Progress;
using JetBrains.ReSharper.Feature.Services.Navigation.ContextNavigation;
using JetBrains.ReSharper.Feature.Services.Navigation.Requests;
using JetBrains.ReSharper.Feature.Services.Occurrences;
using JetBrains.ReSharper.Psi;
using JetBrains.Util.Collections;
using ReSharperPlugin.IntelligentComments.Comments.PSI.DeclaredElements;

namespace ReSharperPlugin.IntelligentComments.Comments.PSI.Features.Navigation;

public class NamedEntitiesSearchRequest : SearchDeclarationsRequest
{
  public NamedEntitiesSearchRequest(DeclaredElementTypeUsageInfo elementInfo) : base(elementInfo)
  {
  }

  
  public override ICollection<IOccurrence> Search(IProgressIndicator progressIndicator)
  {
    if (Target.GetValidDeclaredElement() is not NamedEntityDeclaredElement declaredElement ||
        declaredElement.DeclarationRange.Document.GetPsiSourceFile(Solution) is not { } sourceFile)
    {
      return EnumerableCollection<IOccurrence>.Empty;
    }
    
    return new[]
    {
      new NamedEntityDeclaredElementOccurence(declaredElement.NameWithKind, Solution, sourceFile, declaredElement.DeclarationRange.StartOffset)
    };
  }
}