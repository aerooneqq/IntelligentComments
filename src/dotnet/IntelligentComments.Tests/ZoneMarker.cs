using System.Threading;
using IntelligentComments.Comments;
using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.ReSharper.TestFramework;
using JetBrains.TestFramework;
using JetBrains.TestFramework.Application.Zones;
using NUnit.Framework;

[assembly: TestDataPathBase("../../../data")]
[assembly: Apartment(ApartmentState.STA)]

namespace IntelligentComments.Tests;

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