using JetBrains.Lifetimes;
using JetBrains.ReSharper.FeaturesTestFramework.Daemon;
using JetBrains.ReSharper.TestFramework;
using NUnit.Framework;

namespace ReSharperPlugin.IntelligentComments.Tests.test;

[TestFixture]
public class CommentsHighlightingTest : CSharpHighlightingTestBase
{
  [Test] public void Test1() { DoNamedTest2(); }
}