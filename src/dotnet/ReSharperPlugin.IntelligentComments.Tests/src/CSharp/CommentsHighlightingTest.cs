using System.Collections.Generic;
using JetBrains.Application.Components;
using JetBrains.Application.Settings;
using JetBrains.ProjectModel;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.FeaturesTestFramework.Daemon;
using JetBrains.ReSharper.Psi;
using NUnit.Framework;
using ReSharperPlugin.IntelligentComments.Comments.Daemon;

namespace ReSharperPlugin.IntelligentComments.Tests.CSharp;

[TestFixture]
public class CommentsHighlightingTest : CSharpHighlightingTestBase
{
  protected override string RelativeTestDataPath => @"CSharp\Highlightings";
  
  protected override IReadOnlyCollection<IDaemonStage> GetActiveStages(ISolution solution)
  {
    return new IDaemonStage[]
    {
      new CommentsCollectionStage()
    };
  }

  protected override bool HighlightingPredicate(IHighlighting highlighting, IPsiSourceFile sourceFile,
    IContextBoundSettingsStore settingsStore)
  {
    return highlighting is CommentFoldingHighlighting;
  }
  

  [Test] public void TestSimpleComment() { DoNamedTest2(); }
}