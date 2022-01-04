using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface IReference
{
  [NotNull] string RawValue { get; }


  ResolveResult Resolve(IResolveContext context);
}

public interface IProxyReference : IReference
{
  int RealReferenceId { get; }

  [CanBeNull] 
  IReference GetRealReference(ISolution solution, IDocument contextDocument);
}

public interface IResolveContext
{
  ISolution Solution { get; }
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