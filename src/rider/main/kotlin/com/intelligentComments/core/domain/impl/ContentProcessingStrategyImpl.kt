package com.intelligentComments.core.domain.impl

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider

const val emptyCommentPlaceholder = "..."
class ContentProcessingStrategyImpl : ContentProcessingStrategy {

  override fun process(segments: MutableList<ContentSegment>) {
    return preprocessSegments(segments)
  }

  private fun preprocessSegments(segments: MutableList<ContentSegment>) {
    val settings = RiderIntelligentCommentsSettingsProvider.getInstance()
    removeEmptyRowsAndColsFromTables(settings, segments)
    removeInvalidSegments(segments)
    moveReferencesAndInvariantsToBottom(segments)

    groupSeeAlsoIfNeeded(settings, segments)
    groupReturnsIfNeeded(settings, segments)
    groupParamsIfNeeded(settings, segments)
    groupTypeParamsIfNeeded(settings, segments)
    groupExceptionsIfNeeded(settings, segments)
    groupSummaryIfNeeded(settings, segments)
    groupRemarksIfNeeded(settings, segments)

    groupHacks(segments)
    groupTickets(segments)
    groupTodos(segments)
    groupInvariants(segments)
    groupReferences(segments)

    if (settings.showOnlySummary.value) {
      removeAllSegmentsButSummaries(segments)
      addEmptyContentSegmentIfNeeded(segments)
    }
  }

  private fun groupHacks(segments: MutableList<ContentSegment>) {
    groupSegmentsOfType<HackWithTicketsContentSegment>(segments) { GroupedHacksSegment(it, segments.first().parent )}
  }

  private fun groupTickets(segments: MutableList<ContentSegment>) {
    groupSegmentsOfType<TicketContentSegment>(segments) { GroupedTicketsSegment(it, segments.first().parent )}
  }

  private fun groupSeeAlsoIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupSeeAlso.value) return
    groupSegmentsOfType<SeeAlsoSegment>(segments) { GroupedSeeAlsoSegment(it, segments.first().parent) }
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
    groupSegmentsOfType<ReturnSegment>(segments) { GroupedReturnSegment(it, segments.first().parent) }
  }

  private fun groupTodos(
    segments: MutableList<ContentSegment>
  ) {
    groupSegmentsOfType<ToDoWithTicketsContentSegment>(segments) { GroupedTodosSegment(it, segments.first().parent) }
  }

  private fun groupParamsIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupParams.value) return
    groupSegmentsOfType<ParameterSegment>(segments) { GroupedParamSegment(it, segments.first().parent) }
  }

  private fun groupTypeParamsIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupParams.value) return
    groupSegmentsOfType<TypeParamSegment>(segments) { GroupedTypeParamSegment(it, segments.first().parent) }
  }

  private fun groupExceptionsIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupExceptions.value) return
    groupSegmentsOfType<ExceptionSegment>(segments) { GroupedExceptionsSegment(it, segments.first().parent) }
  }

  private fun groupSummaryIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupSummaries.value) return
    groupSegmentsOfType<SummaryContentSegment>(segments) { GroupedSummarySegment(it, segments.first().parent) }
  }

  private fun groupRemarksIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupRemarks.value) return
    groupSegmentsOfType<RemarksSegment>(segments) { GroupedRemarksSegment(it, segments.first().parent) }
  }

  private fun groupInvariants(
    segments: MutableList<ContentSegment>
  ) {
    groupSegmentsOfType<InvariantSegment>(segments) { GroupedInvariantsSegment(it, segments.first().parent) }
  }

  private fun groupReferences(
    segments: MutableList<ContentSegment>
  ) {
    groupSegmentsOfType<ReferenceContentSegment>(segments) { GroupedReferencesSegment(it, segments.first().parent )}
  }

  private fun removeEmptyRowsAndColsFromTables(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.removeEmptyRowsAndCols.value) return

    visitAllContentSegments(segments) {
      if (it is TableContentSegment) {
        it.removeEmptyRowsAndCols()
      }
    }
  }

  private fun removeInvalidSegments(segments: MutableList<ContentSegment>) {
    removeContentSegmentsRecursively(segments) { !it.isValid() }
  }

  private fun moveReferencesAndInvariantsToBottom(segments: MutableList<ContentSegment>) {
    val segmentsToAddToTail = mutableListOf<ContentSegment>()
    for (segment in segments) {
      if (segment is ReferenceContentSegment || segment is InvariantSegment) {
        segmentsToAddToTail.add(segment)
      }
    }

    segments.removeAll(segmentsToAddToTail)
    segments.addAll(segmentsToAddToTail)
  }

  private fun removeAllSegmentsButSummaries(segments: MutableList<ContentSegment>) {
    segments.removeAll { !(it is SummaryContentSegment || it is GroupedSummarySegment) }
  }

  private fun addEmptyContentSegmentIfNeeded(segments: MutableList<ContentSegment>) {
    if (segments.size != 0) return

    val highlighter = CommonsHighlightersFactory.tryCreateCommentHighlighter(null, emptyCommentPlaceholder.length)
    segments.add(object : UniqueEntityImpl(), TextContentSegment {
      override val highlightedText: HighlightedText = HighlightedTextImpl(emptyCommentPlaceholder, this, highlighter)
      override val parent: Parentable? = null
    })
  }
}