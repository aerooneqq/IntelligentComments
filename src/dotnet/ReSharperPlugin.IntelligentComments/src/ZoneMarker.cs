using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.ProjectModel;
using JetBrains.Rider.Backend.Env;
using JetBrains.Rider.Backend.Product;
using JetBrains.TextControl;

namespace ReSharperPlugin.IntelligentComments
{
  [ZoneMarker]
  public class ZoneMarker : 
    IRequire<IProjectModelZone>, 
    IRequire<ITextControlsZone>, 
    IRequire<IRiderFeatureZone>,
    IRequire<IRiderProductEnvironmentZone>
  {
  }
}