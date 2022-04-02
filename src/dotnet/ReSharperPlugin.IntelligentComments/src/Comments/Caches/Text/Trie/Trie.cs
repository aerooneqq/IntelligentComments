using JetBrains.Annotations;
using JetBrains.Collections;
using JetBrains.Util;
using JetBrains.Util.Logging;
using System;
using System.Collections.Generic;
using System.Linq;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Text.Trie;

public class Trie
{
  [NotNull] private static readonly ILogger ourLogger = Logger.GetLogger<Trie>();

  [NotNull] private readonly Node myRoot;
  
  private int myMaxKeyLength;


  public Trie()
  {
    myRoot = new Node();
  }
  
  
  public void CreatePathIfNeeded([NotNull] string key)
  {
    myMaxKeyLength = Math.Max(key.Length, myMaxKeyLength);
    myRoot.GetOrCreateChild(key, 0);
  }
  
  public bool ContainsKey([NotNull] string key)
  {
    return TryGetNode(key) is { };
  }
  
  public bool SetValue([NotNull] string key, int delta)
  {
    if (TryGetNode(key) is not { } node)
    {
      ourLogger.LogAssertion($"Settings value for child which is does not exist for {key}");
      return false;
    }

    node.Value = delta;
    
    if (node.Value < 0)
    {
      ourLogger.LogAssertion($"The node's value was negative for key {key}");
    }

    return true;
  }
  
  [CanBeNull]
  public int? TryGet([NotNull] string key)
  {
    return TryGetNode(key)?.Value;
  }
  
  [CanBeNull]
  private Node TryGetNode([NotNull] string key)
  {
    var node = myRoot;
    var currentIndex = 0;
    while (currentIndex < key.Length)
    {
      node = node.TryGetChild(key[currentIndex]);
      if (node is null) return null;
      ++currentIndex;
    }

    return node;
  }
  
  [NotNull]
  public IEnumerable<string> GetAllInvariantsNames()
  {
    var result = new HashSet<string>();
    var chars = new char[myMaxKeyLength];
    foreach (var (transitionChar, child) in myRoot.GetChildren())
    {
      CollectAllWordsRecursive(child, transitionChar, chars, 0, result);
    }

    return result;
  }

  [NotNull]
  public IEnumerable<string> GetInvariantNamesStartsWith([NotNull] string prefix)
  {
    if (myMaxKeyLength < prefix.Length || TryGetNode(prefix) is not { } node) return EmptyList<string>.Enumerable;

    var chars = new char[myMaxKeyLength];
    for (var i = 0; i < prefix.Length; i++)
    {
      chars[i] = prefix[i];
    }

    var result = new HashSet<string>();
    var children = node.GetChildren().ToList();
    if (children.Count == 0 && node.Value != 0)
    {
      return new[] { prefix };
    }
    
    foreach (var (childTransitionChar, child) in node.GetChildren())
    {
      CollectAllWordsRecursive(child, childTransitionChar, chars, prefix.Length, result);
    }

    return result;
  }

  private static void CollectAllWordsRecursive(
    [NotNull] Node node, char transitionChar, [NotNull] char[] chars, int index, [NotNull] HashSet<string> result)
  {
    chars[index++] = transitionChar;
    
    if (node.Value != 0)
    {
      var newArray = new char[index];
      Array.Copy(chars, newArray, index);
      result.Add(new string(newArray));
    }
    
    foreach (var (childTransitionChar, child) in node.GetChildren())
    {
      CollectAllWordsRecursive(child, childTransitionChar, chars, index, result);
    }
  }
}