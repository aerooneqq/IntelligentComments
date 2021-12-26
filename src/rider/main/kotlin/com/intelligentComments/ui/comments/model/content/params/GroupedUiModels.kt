package com.intelligentComments.ui.comments.model.content.params

import com.intelligentComments.core.domain.core.CommonsHighlightersFactory
import com.intelligentComments.core.domain.core.ContentSegments
import com.intelligentComments.core.domain.core.HighlightedText
import com.intelligentComments.core.domain.core.HighlightedTextImpl
import com.intelligentComments.core.domain.impl.GroupedParamSegments
import com.intelligentComments.core.domain.impl.GroupedTypeParamSegments
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

class GroupedParamsUiModel(
  project: Project,
  model: GroupedParamSegments
) : GroupedContentUiModel(
  project,
  model,
  model.segments.map {
    object : ContentSegments {
      override val segments = listOf(it)
    }
  },
  getGroupedParamsSectionHeader(project, groupedParamsSectionName, Colors.ParamsSectionHeaderBackgroundColor)
)

private const val groupedParamsSectionName = "Parameters"
private const val groupedTypeParamsSectionName = "Type parameters"

private fun getGroupedParamsSectionHeader(
  project: Project,
  sectionName: String,
  backgroundColor: ColorName,
): HighlightedText {
  val colorsProvider = project.service<ColorsProvider>()
  val textColor = colorsProvider.getColorFor(Colors.TextInSectionsRectanglesHeadersColor)
  val paramBackgroundColor = colorsProvider.getColorFor(backgroundColor)

  val highlighter = CommonsHighlightersFactory.getWithRoundedBackgroundRect(textColor, paramBackgroundColor, sectionName.length)
  return HighlightedTextImpl(sectionName, listOf(highlighter))
}

class GroupedTypeParamsUiModel(
  project: Project,
  model: GroupedTypeParamSegments
) : GroupedContentUiModel(
  project,
  model,
  model.segments.map {
    object : ContentSegments {
      override val segments = listOf(it)
    }
  },
  getGroupedParamsSectionHeader(project, groupedTypeParamsSectionName, Colors.TypeParamNameBackgroundColor)
)