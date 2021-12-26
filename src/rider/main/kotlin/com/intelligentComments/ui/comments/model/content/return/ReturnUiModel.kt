package com.intelligentComments.ui.comments.model.content.`return`

import com.intelligentComments.core.domain.core.CommonsHighlightersFactory
import com.intelligentComments.core.domain.core.HighlightedTextImpl
import com.intelligentComments.core.domain.core.ReturnSegment
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ReturnUiModel(project: Project, ret: ReturnSegment) : ContentSegmentUiModel(project, ret) {
  companion object {
    const val returnSectionName = "Returns"
  }

  val content = ContentSegmentsUiModel(project, ret.content)
  val headerText = HighlightedTextUiWrapper(project, getHeaderHighlightedText())


  private fun getHeaderHighlightedText(): HighlightedTextImpl {
    val textColor = colorsProvider.getColorFor(Colors.TextInSectionsHeadersColor)
    val returnBackgroundColor = colorsProvider.getColorFor(Colors.ReturnBackgroundColor)
    val length = returnSectionName.length

    val highlighter = CommonsHighlightersFactory.getWithRoundedBackgroundRect(textColor, returnBackgroundColor, length)
    return HighlightedTextImpl(returnSectionName, listOf(highlighter))
  }

  override fun hashCode(): Int = HashUtil.hashCode(content.hashCode())
  override fun equals(other: Any?): Boolean = other is ReturnUiModel && other.hashCode() == hashCode()
}