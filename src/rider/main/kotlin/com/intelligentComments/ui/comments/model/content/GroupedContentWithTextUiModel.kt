package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.content.seeAlso.GroupedSeeAlsoUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.awt.Font

open class GroupedUiModel(
  project: Project,
  groupedModel: GroupedContentSegment<*>,
  header: HighlightedText
) : ContentSegmentUiModel(project, groupedModel) {
  val header = HighlightedTextUiWrapper(project, header)
}

open class GroupedContentWithTextUiModel(
  project: Project,
  groupedModel: GroupedContentSegment<*>,
  textSegmentsToMerge: List<HighlightedText>,
  header: HighlightedText
) : GroupedUiModel(project, groupedModel, header) {
  val description: TextContentSegmentUiModel


  init {
    val unitedDescription = HighlightedTextImpl.createEmpty()
    if (textSegmentsToMerge.isNotEmpty()) {
      val delimiter = project.service<RiderIntelligentCommentsSettingsProvider>().groupingDelimiter.value
      unitedDescription.mergeWith(textSegmentsToMerge.first())
      for (index in 1 until textSegmentsToMerge.size) {
        unitedDescription.mergeWith(delimiter).mergeWith(textSegmentsToMerge[index])
      }
    }

    description = TextContentSegmentUiModel(project, object : UniqueEntityImpl(), TextContentSegment {
      override val highlightedText: HighlightedText = unitedDescription
    })
  }


  override fun hashCode(): Int = HashUtil.hashCode(description.hashCode(), header.hashCode())
  override fun equals(other: Any?): Boolean = other is GroupedSeeAlsoUiModel && other.hashCode() == hashCode()
}

open class GroupedContentUiModel(
  project: Project,
  groupedModel: GroupedContentSegment<*>,
  segments: List<ContentSegments>,
  header: HighlightedText
) : GroupedUiModel(project, groupedModel, header) {
  val content = ContentSegmentsUiModel(project, segments.map { it.segments }.flatten())
}

fun getSecondLevelHeader(project: Project, text: String): HighlightedText {
  val colorsProvider = project.service<ColorsProvider>()
  val textColor = colorsProvider.getColorFor(Colors.TextInSectionsHeadersColor)

  val highlighter = TextHighlighterImpl(0, text.length, textColor, TextAttributesImpl(false, 500f, Font.PLAIN))
  return HighlightedTextImpl(text, listOf(highlighter))
}