using System.Threading;
using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.Application.Environment;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.TestFramework;
using JetBrains.TestFramework;
using JetBrains.TestFramework.Application.Zones;
using JetBrains.TextControl;
using NUnit.Framework;

[assembly: TestDataPathBase("../../../data")]
[assembly: Apartment(ApartmentState.STA)]

namespace ReSharperPlugin.IntelligentComments.Tests;

[ZoneMarker]
public class ZoneMarker : IRequire<IntelligentCommentsZone>
{
}

[ZoneActivator]
public class ZoneActivator : IActivate<IntelligentCommentsZone>
{
}

[ZoneDefinition]
public class IntelligentCommentsTestsEnvZone :
  ITestsEnvZone,
  IRequire<IntelligentCommentsZone>,
  IRequire<PsiFeatureTestZone>
{
}

[SetUpFixture]
public class TestEnvironment : ExtensionTestEnvironmentAssembly<IntelligentCommentsTestsEnvZone>
{
  public override bool IsRunningTestsWithAsyncBehaviorProhibited => true;
}