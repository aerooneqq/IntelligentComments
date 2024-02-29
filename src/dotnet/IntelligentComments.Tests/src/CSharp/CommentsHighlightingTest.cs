using System;
using IntelligentComments.Comments.Calculations.Core;
using IntelligentComments.Comments.Daemon;
using JetBrains.Application.Settings;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.FeaturesTestFramework.Daemon;
using JetBrains.ReSharper.Psi;
using NUnit.Framework;

namespace IntelligentComments.Tests.CSharp;

[TestFixture]
public class CommentsHighlightingTest : CSharpHighlightingTestBase
{
  protected override string RelativeTestDataPath => @"CSharp\Highlightings";

  protected override Func<IDaemonStage, bool> GetActiveStagesFilter(ISolution solution) => stage => stage is CommentsCollectionStage;

  protected override bool HighlightingPredicate(
    IHighlighting highlighting, IPsiSourceFile sourceFile, IContextBoundSettingsStore settingsStore)
  {
    return highlighting is CommentFoldingHighlighting or CommentErrorHighlighting;
  }
  

  [Test] public void Test0() { DoNamedTest2(); }
  [Test] public void Test1() { DoNamedTest2(); }
  [Test] public void Test2() { DoNamedTest2(); }
  [Test] public void Test3() { DoNamedTest2(); }
  [Test] public void Test4() { DoNamedTest2(); }
  [Test] public void Test5() { DoNamedTest2(); }
  [Test] public void Test6() { DoNamedTest2(); }
  
  [Test] public void TestSimpleNamedEntitiesDeclarationsAndReferences() { DoNamedTest2(); }
  [Test] public void TestInlineReferences() { DoNamedTest2(); }
  [Test] public void TestInlineReferencesToInlineEntities() { DoNamedTest2(); }
  [Test] public void TestImageSourceAttributeIsNotSet() { DoNamedTest2(); }
  [Test] public void TestImageSourceNotSetError() { DoNamedTest2(); }
  [Test] public void TestInvariantNameNotSet() { DoNamedTest2(); }
  [Test] public void TestReferenceSourceIsNotSet() { DoNamedTest2(); }
  [Test] public void TestReferenceSourceIsUnresolved() { DoNamedTest2(); }
  [Test] public void TestNamedEntityDeclaredMoreThanOnceError() { DoNamedTest2(); }
  [Test] public void TestNotWhiteListedAttributes() { DoNamedTest2(); }
  [Test] public void TestGroupOfLineCommentsWithSpecialComments() { DoNamedTest2(); }
}