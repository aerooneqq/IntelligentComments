using JetBrains.ProjectModel;
using JetBrains.RdBackend.Common.Features;
using JetBrains.Rider.Model;

namespace ReSharperPlugin.IntelligentComments.Protocol;

[SolutionComponent]
public class ProtocolModelRegistrar
{
  public ProtocolModelRegistrar(ISolution solution)
  {
    var protoSerializers = solution.GetProtocolSolution().Proto.Serializers;
    protoSerializers.RegisterToplevelOnce(typeof(RdCommentsModel), RdCommentsModel.RegisterDeclaredTypesSerializers);
  }
}