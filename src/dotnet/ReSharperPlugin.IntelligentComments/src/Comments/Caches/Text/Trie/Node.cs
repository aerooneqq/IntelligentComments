using JetBrains.Annotations;
using System;
using System.Collections.Generic;
using System.Linq;

namespace ReSharperPlugin.IntelligentComments.Comments.Caches.Text.Trie;

public class Node
{
  [NotNull] private readonly Dictionary<char, Node> myChildren;
  
  public int Value { get; set; }

  
  public Node()
  {
    myChildren = new Dictionary<char, Node>();
  }
  
  
  [NotNull]
  public Node GetOrCreateChild(string key, int currentCharIndex)
  {
    if (currentCharIndex == key.Length)
    {
      return this;
    }

    var @char = key[currentCharIndex];

    Node nextChild;
    if (myChildren.TryGetValue(@char, out var child))
    {
      nextChild = child;
    }
    else
    {
      nextChild = new Node();
      myChildren[@char] = nextChild;
    }
    
    return nextChild.GetOrCreateChild(key, currentCharIndex + 1);
  }

  [CanBeNull]
  public Node TryGetChild(char c)
  {
    return myChildren.TryGetValue(c, out var node) ? node : null;
  }

  [NotNull] public IEnumerable<KeyValuePair<char, Node>> GetChildren() => myChildren;

  
  [NotNull]
  public IEnumerable<Node> GetSubtree()
  {
    return Enumerable.Repeat(this, 1).Concat(myChildren.SelectMany(pair => pair.Value.GetSubtree()));
  }
}