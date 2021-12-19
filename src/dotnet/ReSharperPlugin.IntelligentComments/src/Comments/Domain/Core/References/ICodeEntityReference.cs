using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Domain.Core.References;

public interface ICodeEntityReference : IReference
{
  [NotNull] string RawMemberName { get; }
}