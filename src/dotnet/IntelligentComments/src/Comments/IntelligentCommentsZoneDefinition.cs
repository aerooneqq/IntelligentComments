using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.DocumentModel;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Daemon.Syntax;
using JetBrains.ReSharper.Feature.Services;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Features.ReSpeller;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.TextControl;

namespace IntelligentComments.Comments;

[ZoneDefinition]
public class IntelligentCommentsZone : 
  IZone,
  IRequire<IPsiLanguageZone>,
  IRequire<IProjectModelZone>,
  IRequire<ITextControlsZone>,
  IRequire<ILanguageCSharpZone>,
  IRequire<IDocumentModelZone>,
  IRequire<DaemonZone>,
  IRequire<ICodeEditingZone>,
  IRequire<IReSpellerZone>,
  IRequire<ISyntaxHighlightingZone>
{
}