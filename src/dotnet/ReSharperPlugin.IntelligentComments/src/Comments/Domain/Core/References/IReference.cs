using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface IReference
{
  [NotNull] string RawValue { get; }


  ResolveResult Resolve();
}

public abstract class ResolveResult
{
  
}

public class EmptyResolveResult : ResolveResult
{
  public static EmptyResolveResult Instance { get; } = new EmptyResolveResult();
  
  
  private EmptyResolveResult()
  {
  }
}