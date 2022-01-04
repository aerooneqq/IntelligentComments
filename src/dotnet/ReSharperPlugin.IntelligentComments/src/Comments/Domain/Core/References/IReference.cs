using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.Tree;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface IReference
{
  [NotNull] string RawValue { get; }


  ResolveResult Resolve(IResolveContext context);
}

public interface IResolveContext
{
  public ISolution Solution { get; }
}

public abstract class ResolveResult
{
}

public class EmptyResolveResult : ResolveResult
{
  public static EmptyResolveResult Instance { get; } = new();
  
  
  private EmptyResolveResult()
  {
  }
}