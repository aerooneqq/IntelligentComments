package com.intelligentComments.ui.comments.model.content.params

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intellij.openapi.project.Project

class ParameterUiModel(
  project: Project,
  private val parameter: ParameterSegment
) : ContentSegmentUiModel(project, parameter) {
  val name = HighlightedTextUiWrapper(project, getHighlightedName())

  private fun getHighlightedName(): HighlightedText {
    val textColor = colorsProvider.getColorFor(Colors.TextInSectionsHeadersColor)
    val paramBackgroundColor = colorsProvider.getColorFor(Colors.ParamNameBackgroundColor)

    val highlighter = CommonsHighlightersFactory.getWithRoundedBackgroundRect(textColor, paramBackgroundColor, parameter.name.length)
    return HighlightedTextImpl(parameter.name, listOf(highlighter))
  }

  val description = ContentSegmentsUiModel(project, parameter.content)
}