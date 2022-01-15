using JetBrains.Application;
using JetBrains.ProjectModel;
using JetBrains.Rd;
using JetBrains.RdBackend.Common.Features;
using JetBrains.Rider.Model;

namespace ReSharperPlugin.IntelligentComments.Protocol;

[SolutionComponent]
public class ProtocolModelRegistrar
{
  public ProtocolModelRegistrar(ISolution solution)
  {
    ISerializers protoSerializers = solution.GetProtocolSolution().Proto.Serializers;
    protoSerializers.RegisterToplevelOnce(typeof(RdCommentsModel), RdCommentsModel.RegisterDeclaredTypesSerializers);
  }
}