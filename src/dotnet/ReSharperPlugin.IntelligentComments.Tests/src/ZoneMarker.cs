using System.Threading;
using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.ReSharper.Features.ReSpeller;
using JetBrains.ReSharper.TestFramework;
using JetBrains.TestFramework;
using JetBrains.TestFramework.Application.Zones;
using NUnit.Framework;

[assembly: TestDataPathBase("../../../data")]
[assembly: Apartment(ApartmentState.STA)]

namespace ReSharperPlugin.IntelligentComments.Tests;

[ZoneDefinition]
public class IntelligentCommentsTestsEnvZone :
  ITestsEnvZone,
  IRequire<IReSpellerZone>,
  IRequire<IntelligentCommentsZone>,
  IRequire<PsiFeatureTestZone>
{
}

[SetUpFixture]
public class TestEnvironment : ExtensionTestEnvironmentAssembly<IntelligentCommentsTestsEnvZone>
{
  public override bool IsRunningTestsWithAsyncBehaviorProhibited => true;
}