using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface IDomainReference
{
  [NotNull] string RawValue { get; }


  [NotNull] ResolveResult Resolve(IResolveContext context);
}

public interface IProxyDomainReference : IDomainReference
{
  int RealReferenceId { get; }
}

public interface IResolveContext
{
  [NotNull] ISolution Solution { get; }
  [CanBeNull] IDocument Document { get; }
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

public class InvalidResolveResult : ResolveResult
{
  public string Error { get; }

  
  public InvalidResolveResult(string error)
  {
    Error = error;
  }
}