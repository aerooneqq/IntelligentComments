package com.intelligentComments.ui.comments.model.content.exceptions

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

class GroupedExceptionUiModel(
  project: Project,
  model: GroupedContentSegment<ExceptionSegment>
) : GroupedContentUiModel(
  project,
  model,
  model.segments.map {
    object : ContentSegments {
      override val segments: Collection<ContentSegment> = listOf(it)
    }
  },
  getHeaderHighlightedText(project)
)

private const val exceptionsSectionName = "Exceptions"

private fun getHeaderHighlightedText(project: Project): HighlightedTextImpl {
  val colorsProvider = project.service<ColorsProvider>()
  val textColor = colorsProvider.getColorFor(Colors.TextInSectionsRectanglesHeadersColor)
  val returnBackgroundColor = colorsProvider.getColorFor(Colors.ExceptionBackgroundColor)

  val length = exceptionsSectionName.length
  val highlighter = CommonsHighlightersFactory.getWithRoundedBackgroundRect(textColor, returnBackgroundColor, length)

  return HighlightedTextImpl(exceptionsSectionName, listOf(highlighter))
}