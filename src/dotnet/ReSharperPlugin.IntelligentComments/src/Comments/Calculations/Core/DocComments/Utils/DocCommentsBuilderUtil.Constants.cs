using System.Collections.Generic;
using JetBrains.Annotations;

namespace ReSharperPlugin.IntelligentComments.Comments.Calculations.Core.DocComments.Utils;

internal static partial class DocCommentsBuilderUtil
{
  [NotNull] internal const string ImageTagName = "image";
  [NotNull] internal const string ImageSourceAttrName = "source";
  [NotNull] internal const string ReferenceTagName = "reference";
  [NotNull] internal const string TodoTagName = "todo";
  [NotNull] internal const string DescriptionTagName = "description";
  [NotNull] internal const string TicketsSectionTagName = "tickets";
  [NotNull] internal const string TicketTagName = "ticket";
  [NotNull] internal const string HackTagName = "hack";
  [NotNull] internal const string InvariantTagName = "invariant";

  [NotNull] internal const string CommonNameAttrName = "name";
  [NotNull] internal const string TicketNameAttrName = CommonNameAttrName;
  [NotNull] internal const string HackNameAttrName = CommonNameAttrName;
  [NotNull] internal const string TicketSourceAttrName = "source";
  
  [NotNull] internal const string InvariantReferenceSourceAttrName = "invariant";
  [NotNull] internal const string HackReferenceSourceAttributeName = "hack";
  [NotNull] internal const string TodoReferenceSourceAttributeName = "todo";
  
  [NotNull] internal const string InvariantNameAttrName = CommonNameAttrName;
  [NotNull] internal const string InheritDocTagName = "inheritdoc";
  [NotNull] internal const string CRef = "cref";


  internal static HashSet<string> PossibleNamedEntityTags { get; } = new()
  {
    TodoTagName,
    HackTagName,
    InvariantTagName
  };

  internal static HashSet<string> PossibleTicketAttributes { get; } = new()
  {
    TicketSourceAttrName
  };

  [NotNull]
  internal static HashSet<string> PossibleInnerFirstLevelTagsOfTodo { get; } = new()
  {
    DescriptionTagName,
    TicketsSectionTagName
  };

  [NotNull]
  internal static HashSet<string> PossibleInnerFirstLevelTagsOfHack { get; } = new()
  {
    DescriptionTagName,
    TicketsSectionTagName
  };

  [NotNull]
  internal static HashSet<string> PossibleInnerFirstLevelTagsOfTicketsSection { get; } = new()
  {
    TicketTagName
  };

  [NotNull] 
  internal static HashSet<string> PossibleReferenceTagSourceAttributes { get; } = new() 
  {
    InvariantReferenceSourceAttrName,
    HackReferenceSourceAttributeName,
    TodoReferenceSourceAttributeName
  };

  [NotNull] 
  internal static string PossibleReferenceTagAttributesPresentation { get; } = string.Join(", ", PossibleReferenceTagSourceAttributes);


  [NotNull] private static readonly ISet<char> ourCharsWithNoNeedToAddSpaceAfter = new HashSet<char>
  {
    '(', '[', '{',
  };
  
  [NotNull] private static readonly ISet<char> ourWhitespaceChars = new HashSet<char> { ' ', '\n', '\r', '\t' };
}