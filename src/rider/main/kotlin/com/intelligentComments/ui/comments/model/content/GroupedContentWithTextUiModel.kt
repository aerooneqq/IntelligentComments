package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.awt.Font

abstract class GroupedUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  groupedModel: GroupedContentSegment<*>,
  header: HighlightedText
) : ContentSegmentUiModel(project, parent, groupedModel) {
  val header = HighlightedTextUiWrapper(project, this, header)
}

abstract class GroupedContentWithTextUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  groupedModel: GroupedContentSegment<*>,
  textSegmentsToMerge: List<HighlightedText>,
  header: HighlightedText
) : GroupedUiModel(project, parent, groupedModel, header) {
  val description: TextContentSegmentUiModel


  init {
    val unitedDescription = HighlightedTextImpl.createEmpty(groupedModel)
    if (textSegmentsToMerge.isNotEmpty()) {
      val delimiter = RiderIntelligentCommentsSettingsProvider.getInstance().groupingDelimiter.value
      unitedDescription.mergeWith(textSegmentsToMerge.first())
      for (index in 1 until textSegmentsToMerge.size) {
        unitedDescription.mergeWith(delimiter).mergeWith(textSegmentsToMerge[index])
      }
    }

    description = TextContentSegmentUiModel(project, this, object : UniqueEntityImpl(), TextContentSegment {
      override val highlightedText: HighlightedText = unitedDescription
      override val parent: Parentable = this
    })
  }


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(description.hashCode(), header.hashCode())
  }
}

abstract class GroupedContentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  groupedModel: GroupedContentSegment<*>,
  segments: List<ContentSegments>,
  header: HighlightedText
) : GroupedUiModel(project, parent, groupedModel, header) {
  val content = ContentSegmentsUiModel(project, this, segments.map { it.segments }.flatten())

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(header.calculateStateHash(), content.calculateStateHash())
  }
}

fun getSecondLevelHeader(project: Project, text: String, parent: Parentable): HighlightedText {
  val colorsProvider = project.service<ColorsProvider>()
  val textColor = colorsProvider.getColorFor(Colors.TextInSectionsHeadersColor)

  val attributes = TextAttributesImpl(false, 600f, Font.PLAIN)
  val highlighter = CommonsHighlightersFactory.createHighlighter(text.length, textColor, attributes)
  return HighlightedTextImpl(text, parent, listOf(highlighter))
}

fun getFirstLevelHeader(
  project: Project,
  text: String,
  parent: Parentable
): HighlightedText {
  val colorsProvider = project.service<ColorsProvider>()
  val textColor = colorsProvider.getColorFor(Colors.TextInSectionsHeadersColor)
  val attributes = TextAttributesImpl(true, 600f, Font.PLAIN)
  val highlighter = CommonsHighlightersFactory.createHighlighter(text.length, textColor, attributes)

  return HighlightedTextImpl(text, parent, listOf(highlighter))
}