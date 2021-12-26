package com.intelligentComments.ui.comments.model.content.seeAlso

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.util.*
import kotlin.collections.HashMap

class GroupedSeeAlsoUiModel(
  project: Project,
  private val groupedSeeAlso: GroupedSeeAlsoContentSegment
) : ContentSegmentUiModel(project, groupedSeeAlso) {
  val header = HighlightedTextUiWrapper(project, getSeeAlsoHeaderText(project))
  val description = TextContentSegmentUiModel(project, object : UniqueEntityImpl(), TextContentSegment {
    override val highlightedText: HighlightedText = initDescription()
  })

  private fun initDescription(): HighlightedText {
    val unitedDescription = HighlightedTextImpl.createEmpty()
    val segments = groupedSeeAlso.segments
    if (segments.isEmpty()) return unitedDescription

    val delimiter = project.service<RiderIntelligentCommentsSettingsProvider>().groupingDelimiter.value
    unitedDescription.mergeWith(segments.first().description)
    for (index in 1 until segments.size) {
      unitedDescription.mergeWith(delimiter).mergeWith(segments[index].description)
    }

    return unitedDescription
  }

  override fun hashCode(): Int = HashUtil.hashCode(description.hashCode(), header.hashCode())
  override fun equals(other: Any?): Boolean = other is GroupedSeeAlsoUiModel && other.hashCode() == hashCode()
}