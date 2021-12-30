package com.intelligentComments.core.domain.impl

import com.intelligentComments.core.domain.core.*

class GroupedSeeAlsoSegments(
  override val segments: List<SeeAlsoSegment>
) : UniqueEntityImpl(), GroupedContentSegment<SeeAlsoSegment>

class GroupedReturnSegments(
  override val segments: List<ReturnSegment>
) : UniqueEntityImpl(), GroupedContentSegment<ReturnSegment>

class GroupedParamSegments(
  override val segments: List<ParameterSegment>
) : UniqueEntityImpl(), GroupedContentSegment<ParameterSegment>

class GroupedTypeParamSegments(
  override val segments: List<TypeParamSegment>
) : UniqueEntityImpl(), GroupedContentSegment<TypeParamSegment>

class GroupedExceptionsSegments(
  override val segments: List<ExceptionSegment>
) : UniqueEntityImpl(), GroupedContentSegment<ExceptionSegment>

class GroupedSummarySegments(
  override val segments: List<SummaryContentSegment>
) : UniqueEntityImpl(), GroupedContentSegment<SummaryContentSegment>

class GroupedRemarksSegments(
  override val segments: List<RemarksSegment>
) : UniqueEntityImpl(), GroupedContentSegment<RemarksSegment>