using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.Rider.Backend.Env;
using JetBrains.Rider.Backend.Product;
using JetBrains.TextControl;

namespace ReSharperPlugin.IntelligentComments;

[ZoneDefinition]
public class RiderIntelligentCommentsZone : IZone,
  IRequire<IRiderFeatureZone>,
  IRequire<IRiderProductEnvironmentZone>,
  IRequire<IRiderPlatformZone>,
  IRequire<IntelligentCommentsZone>
{
}

[ZoneDefinition]
public class IntelligentCommentsZone : IZone,
  IRequire<IPsiLanguageZone>,
  IRequire<IProjectModelZone>, 
  IRequire<ITextControlsZone>,
  IRequire<ILanguageCSharpZone>,
  IRequire<IDocumentModelZone>,
  IRequire<DaemonZone>,
  IRequire<ICodeEditingZone>
{
}