package com.intelligentComments.core.domain.impl

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

class ContentProcessingStrategyImpl(private val project: Project) : ContentProcessingStrategy {

  override fun process(segments: MutableList<ContentSegment>) {
    return preprocessSegments(segments)
  }

  private fun preprocessSegments(segments: MutableList<ContentSegment>) {
    val settings = project.service<RiderIntelligentCommentsSettingsProvider>()

    groupSeeAlsoIfNeeded(settings, segments)
    groupReturnsIfNeeded(settings, segments)
    groupParamsIfNeeded(settings, segments)
    groupTypeParamsIfNeeded(settings, segments)
    groupExceptionsIfNeeded(settings, segments)
    groupSummaryIfNeeded(settings, segments)
    groupRemarksIfNeeded(settings, segments)
  }

  private fun groupSeeAlsoIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupSeeAlso.value) return
    groupSegmentsOfType<SeeAlsoSegment>(segments) { GroupedSeeAlsoSegments(it) }
  }

  private inline fun <reified T : ContentSegment> groupSegmentsOfType(
    segments: MutableList<ContentSegment>,
    groupedSegmentFactory: (List<T>) -> GroupedContentSegment<T>
  ) {
    val topLevelSegments = segments.filterIsInstance<T>()
    if (topLevelSegments.isEmpty()) return

    val groupedSegment = groupedSegmentFactory(topLevelSegments.toList())

    val indexOfLastElement = segments.indexOf(topLevelSegments.last())
    segments[indexOfLastElement] = groupedSegment
    segments.removeAll(topLevelSegments)
  }

  private fun groupReturnsIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupReturns.value) return
    groupSegmentsOfType<ReturnSegment>(segments) { GroupedReturnSegments(it) }
  }

  private fun groupParamsIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupParams.value) return
    groupSegmentsOfType<ParameterSegment>(segments) { GroupedParamSegments(it) }
  }

  private fun groupTypeParamsIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupParams.value) return
    groupSegmentsOfType<TypeParamSegment>(segments) { GroupedTypeParamSegments(it) }
  }

  private fun groupExceptionsIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupExceptions.value) return
    groupSegmentsOfType<ExceptionSegment>(segments) { GroupedExceptionsSegments(it) }
  }

  private fun groupSummaryIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupSummary.value) return
    groupSegmentsOfType<SummaryContentSegment>(segments) { GroupedSummarySegments(it) }
  }

  private fun groupRemarksIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupRemarks.value) return
    groupSegmentsOfType<RemarksSegment>(segments) { GroupedRemarksSegments(it) }
  }
}