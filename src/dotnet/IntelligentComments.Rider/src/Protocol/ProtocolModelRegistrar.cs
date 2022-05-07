using JetBrains.Annotations;
using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features;
using JetBrains.Rider.Model;

namespace IntelligentComments.Rider.Protocol;

[SolutionComponent]
public class ProtocolModelRegistrar
{
  public ProtocolModelRegistrar([NotNull] ISolution solution)
  {
    var protoSerializers = solution.GetProtocolSolution().Proto.Serializers;
    protoSerializers.RegisterToplevelOnce(typeof(RdCommentsModel), RdCommentsModel.RegisterDeclaredTypesSerializers);
  }
}