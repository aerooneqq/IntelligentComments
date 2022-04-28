using System;
using System.Linq;
using JetBrains.Annotations;
using NUnit.Framework;

namespace ReSharperPlugin.IntelligentComments.Tests.Trie;

[TestFixture]
public class TrieTest
{
  // ReSharper disable once NotNullMemberIsNotInitialized
  [NotNull] private Comments.Caches.Text.Trie.Trie myTrie;

  [SetUp]
  public void SetUp()
  {
    myTrie = new Comments.Caches.Text.Trie.Trie();
  }
  

  [Test]
  public void SimpleTest()
  {
    AddWordsAndPerformTest(new[] { "hello", "world" });
  }

  private void AddWordsAndPerformTest([ItemNotNull] [NotNull] string[] words)
  {
    foreach (var word in words)
    {
      myTrie.CreatePathIfNeeded(word);
      myTrie.ApplyDelta(word, 1);
    }
    
    foreach (var word in words)
    {
      Assert.IsTrue(myTrie.TryGet(word) is 1);
      
      foreach (var prefix in Enumerable.Range(1, word.Length - 1).Select(index => word[..index]))
      {
        Console.WriteLine(prefix);
        Assert.IsFalse(myTrie.TryGet(word) is 0);
      }
    }
  }
}