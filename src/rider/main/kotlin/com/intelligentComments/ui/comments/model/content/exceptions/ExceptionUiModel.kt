package com.intelligentComments.ui.comments.model.content.exceptions

import com.intelligentComments.core.domain.core.CommonsHighlightersFactory
import com.intelligentComments.core.domain.core.ExceptionSegment
import com.intelligentComments.core.domain.core.HighlightedTextImpl
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.content.`return`.ReturnUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ExceptionUiModel(
  project: Project,
  private val exceptionSegment: ExceptionSegment
) : ContentSegmentUiModel(project, exceptionSegment) {
  val name = HighlightedTextUiWrapper(project, getHeaderHighlightedText())
  val content = ContentSegmentsUiModel(project, exceptionSegment.content)

  private fun getHeaderHighlightedText(): HighlightedTextImpl {
    val textColor = colorsProvider.getColorFor(Colors.TextInSectionsHeadersColor)
    val returnBackgroundColor = colorsProvider.getColorFor(Colors.ExceptionBackgroundColor)
    val length = exceptionSegment.name.length

    val highlighter = CommonsHighlightersFactory.getWithRoundedBackgroundRect(textColor, returnBackgroundColor, length)
    return HighlightedTextImpl(exceptionSegment.name, listOf(highlighter))
  }

  override fun hashCode(): Int = HashUtil.hashCode(name.hashCode(), content.hashCode())

  override fun equals(other: Any?): Boolean = other is ExceptionUiModel && other.hashCode() == hashCode()
}