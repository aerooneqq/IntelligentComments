package com.intelligentComments.core.domain.impl

import com.intelligentComments.core.domain.core.*


open class GroupedSegmentsBase(override val parent: Parentable?) : UniqueEntityImpl(), Parentable

class GroupedSeeAlsoSegments(
  override val segments: List<SeeAlsoSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<SeeAlsoSegment>

class GroupedReturnSegments(
  override val segments: List<ReturnSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ReturnSegment>

class GroupedParamSegments(
  override val segments: List<ParameterSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ParameterSegment>

class GroupedTypeParamSegments(
  override val segments: List<TypeParamSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<TypeParamSegment>

class GroupedExceptionsSegments(
  override val segments: List<ExceptionSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ExceptionSegment>

class GroupedSummarySegments(
  override val segments: List<SummaryContentSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<SummaryContentSegment>

class GroupedRemarksSegments(
  override val segments: List<RemarksSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<RemarksSegment>