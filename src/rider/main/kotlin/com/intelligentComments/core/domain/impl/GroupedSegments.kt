package com.intelligentComments.core.domain.impl

import com.intelligentComments.core.domain.core.*


open class GroupedSegmentsBase(override val parent: Parentable?) : UniqueEntityImpl(), Parentable

class GroupedSeeAlsoSegment(
  override val segments: List<SeeAlsoSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<SeeAlsoSegment>

class GroupedReturnSegment(
  override val segments: List<ReturnSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ReturnSegment>

class GroupedParamSegment(
  override val segments: List<ParameterSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ParameterSegment>

class GroupedTypeParamSegment(
  override val segments: List<TypeParamSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<TypeParamSegment>

class GroupedExceptionsSegment(
  override val segments: List<ExceptionSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ExceptionSegment>

class GroupedSummarySegment(
  override val segments: List<SummaryContentSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<SummaryContentSegment>

class GroupedRemarksSegment(
  override val segments: List<RemarksSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<RemarksSegment>

class GroupedInvariantsSegment(
  override val segments: List<InvariantSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<InvariantSegment>

class GroupedReferencesSegment(
  override val segments: List<ReferenceContentSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ReferenceContentSegment>

class GroupedTodosSegment(
  override val segments: List<ToDoWithTicketsContentSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ToDoWithTicketsContentSegment>

class GroupedTicketsSegment(
  override val segments: List<TicketContentSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<TicketContentSegment>

class GroupedHacksSegment(
  override val segments: List<HackWithTicketsContentSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<HackWithTicketsContentSegment>