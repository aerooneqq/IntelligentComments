package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentComments.ui.colors.ColorName
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
) : ContentSegmentUiModel(project, parent) {
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
  return HighlightedTextImpl(text, parent, tryGetHighlighter(project, text.length, parent))
}

private fun tryGetHighlighter(
  project: Project,
  length: Int,
  parent: Parentable,
  explicitlySetColor: ColorName? = null
): TextHighlighter? {
  val colorsProvider = project.service<ColorsProvider>()
  val attributes = TextAttributesImpl(false, 600f, Font.PLAIN)
  return if (explicitlySetColor != null) {
    val textColor = colorsProvider.getColorFor(explicitlySetColor)
    CommonsHighlightersFactory.createHighlighter(length, textColor, attributes)
  } else {
    CommonsHighlightersFactory.tryCreateCommentHighlighter(parent, length)
  }
}

fun getFirstLevelHeader(
  project: Project,
  text: String,
  parent: Parentable,
  explicitlySetColor: ColorName? = null
): HighlightedText {
  val adjustedText = "$text:"
  return HighlightedTextImpl(adjustedText, parent, tryGetHighlighter(project, adjustedText.length, parent, explicitlySetColor))
}