package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intellij.openapi.project.Project
import java.awt.Color
import java.util.*

class ParameterUiModel(
  project: Project,
  private val parameter: ParameterSegment
) : ContentSegmentUiModel(project, parameter) {
  val name = HighlightedTextUiWrapper(project, getHighlightedName())

  private fun getHighlightedName(): HighlightedText {
    val textColor = colorsProvider.getColorFor(Colors.TextDefaultColor)

    val highlighter = object : TextHighlighter {
      override val startOffset: Int = 0
      override val endOffset: Int = parameter.name.length
      override val attributes: TextAttributes = TextAttributesImpl.defaultAttributes
      override val textColor: Color = textColor
      override val backgroundStyle: BackgroundStyle = BackgroundStyleImpl(Color.RED, true)
      override val mouseInOutAnimation: MouseInOutAnimation? = null
      override val id: UUID = UUID.randomUUID()
    }

    return HighlightedTextImpl(parameter.name, listOf(highlighter))
  }

  val description = ContentSegmentsUiModel(project, parameter.content)
}