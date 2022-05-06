using System.Collections.Generic;
using System.Collections.Immutable;
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
  [NotNull] internal const string ParamTagName = "param";
  [NotNull] internal const string TypeParamTagName = "typeparam";
  [NotNull] internal const string ParamRefTagName = "paramref";
  [NotNull] internal const string TypeParamRefTagName = "typeparamref";
  [NotNull] internal const string ListTagName = "list";

  [NotNull] internal const string ListTypeAttributeName = "type";
  [NotNull] internal const string ListTableType = "table";
  [NotNull] internal const string ListNumberType = "number";
  [NotNull] internal const string ListBulletType = "bullet";

  [NotNull] internal const string CommonNameAttrName = "name";
  [NotNull] internal const string TicketNameAttrName = CommonNameAttrName;
  [NotNull] internal const string HackNameAttrName = CommonNameAttrName;
  [NotNull] internal const string TodoNameAttrName = CommonNameAttrName;
  [NotNull] internal const string TicketSourceAttrName = "source";
  
  [NotNull] internal const string InvariantReferenceSourceAttrName = "invariant";
  [NotNull] internal const string HackReferenceSourceAttributeName = "hack";
  [NotNull] internal const string TodoReferenceSourceAttributeName = "todo";

  [NotNull] internal const string ParamNameAttrName = CommonNameAttrName;
  [NotNull] internal const string ParamRefNameAttrName = CommonNameAttrName;
  [NotNull] internal const string TypeParamNameAttrName = CommonNameAttrName;
  [NotNull] internal const string TypeParamRefNameAttrName = CommonNameAttrName;
  
  [NotNull] internal const string InvariantNameAttrName = CommonNameAttrName;
  [NotNull] internal const string InheritDocTagName = "inheritdoc";
  [NotNull] internal const string CRef = "cref";


  [NotNull]
  internal static IReadOnlySet<string> PossibleNamedEntityTags { get; } = new JetHashSet<string>()
  {
    TodoTagName,
    HackTagName,
    InvariantTagName
  };

  [NotNull]
  internal static IReadOnlySet<string> PossibleNamedEntityTagAttributes { get; } = new JetHashSet<string>()
  {
    CommonNameAttrName
  };

  [NotNull]
  internal static IReadOnlySet<string> PossibleTicketAttributes { get; } = new JetHashSet<string>()
  {
    TicketSourceAttrName
  };

  [NotNull]
  internal static IReadOnlySet<string> PossibleInnerFirstLevelTagsOfTodo { get; } = new JetHashSet<string>()
  {
    DescriptionTagName,
    TicketsSectionTagName
  };

  [NotNull]
  internal static IReadOnlySet<string> PossibleInnerFirstLevelTagsOfHack { get; } = new JetHashSet<string>()
  {
    DescriptionTagName,
    TicketsSectionTagName
  };

  [NotNull]
  internal static IReadOnlySet<string> PossibleInnerFirstLevelTagsOfTicketsSection { get; } = new JetHashSet<string>()
  {
    TicketTagName
  };

  [NotNull] 
  internal static IReadOnlySet<string> PossibleReferenceTagSourceAttributes { get; } = new JetHashSet<string>() 
  {
    InvariantReferenceSourceAttrName,
    HackReferenceSourceAttributeName,
    TodoReferenceSourceAttributeName
  };

  [NotNull] 
  internal static string PossibleReferenceTagAttributesPresentation { get; } = string.Join(", ", PossibleReferenceTagSourceAttributes);


  [NotNull] private static readonly IReadOnlySet<char> ourCharsWithNoNeedToAddSpaceAfter = new JetHashSet<char>
  {
    '(', '[', '{',
  };
  
  [NotNull] private static readonly IReadOnlySet<char> ourWhitespaceChars = new JetHashSet<char> { ' ', '\n', '\r', '\t' };
}