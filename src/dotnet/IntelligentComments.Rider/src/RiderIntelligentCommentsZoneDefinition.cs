using IntelligentComments.Comments;
using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.Rider.Backend.Env;
using JetBrains.Rider.Backend.Product;

namespace IntelligentComments.Rider;

[ZoneDefinition]
public class RiderIntelligentCommentsZone : 
  IZone,
  IRequire<IRiderFeatureZone>,
  IRequire<IRiderProductEnvironmentZone>,
  IRequire<IRiderPlatformZone>,
  IRequire<IntelligentCommentsZone>
{
}