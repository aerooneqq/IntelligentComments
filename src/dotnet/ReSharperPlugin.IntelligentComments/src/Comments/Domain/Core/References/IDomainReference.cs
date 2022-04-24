using JetBrains.Annotations;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.Rd.Base;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface IDomainReference : IPrintable
{
  [NotNull] string RawValue { get; }


  [NotNull] DomainResolveResult Resolve([NotNull] IDomainResolveContext context);
}

public interface IProxyDomainReference : IDomainReference
{
  int RealReferenceId { get; }
}

public interface IDomainResolveContext
{
  [NotNull] ISolution Solution { get; }
  [CanBeNull] IDocument Document { get; }
}

public abstract class DomainResolveResult
{
}

public class EmptyDomainResolveResult : DomainResolveResult
{
  public static EmptyDomainResolveResult Instance { get; } = new();
  
  
  private EmptyDomainResolveResult()
  {
  }
}

public class InvalidDomainResolveResult : DomainResolveResult
{
  public string Error { get; }

  
  public InvalidDomainResolveResult(string error)
  {
    Error = error;
  }
}