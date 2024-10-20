using JetBrains.Annotations;
using JetBrains.Application.Parts;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Protocol;
using JetBrains.Rider.Model;

namespace IntelligentComments.Rider.Protocol;

[SolutionComponent(Instantiation.DemandAnyThreadSafe)]
public class ProtocolModelRegistrar
{
  public ProtocolModelRegistrar([NotNull] ISolution solution)
  {
    var protoSerializers = solution.GetProtocolSolution().TryGetProto()!.Serializers;
    protoSerializers.RegisterToplevelOnce(typeof(RdCommentsModel), RdCommentsModel.RegisterDeclaredTypesSerializers);
  }
}