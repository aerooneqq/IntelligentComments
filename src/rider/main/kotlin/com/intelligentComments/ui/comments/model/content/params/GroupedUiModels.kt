package com.intelligentComments.ui.comments.model.content.params

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.GroupedParamSegments
import com.intelligentComments.core.domain.impl.GroupedTypeParamSegments
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

class GroupedParamsUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedParamSegments
) : GroupedContentUiModel(
  project,
  parent,
  model,
  model.segments.map {
    object : ContentSegments {
      override val segments = listOf(it)
      override val parent: Parentable = model
    }
  },
  getGroupedParamsSectionHeader(project, groupedParamsSectionName, Colors.ParamsSectionHeaderBackgroundColor, model)
)

private const val groupedParamsSectionName = "Parameters"
private const val groupedTypeParamsSectionName = "Type parameters"

private fun getGroupedParamsSectionHeader(
  project: Project,
  sectionName: String,
  backgroundColor: ColorName,
  parent: Parentable
): HighlightedText {
  val colorsProvider = project.service<ColorsProvider>()
  val textColor = colorsProvider.getColorFor(Colors.TextInSectionsRectanglesHeadersColor)
  val paramBackgroundColor = colorsProvider.getColorFor(backgroundColor)

  val highlighter = CommonsHighlightersFactory.getWithRoundedBackgroundRect(
    null, textColor, paramBackgroundColor, sectionName.length
  )

  return HighlightedTextImpl(sectionName, parent, listOf(highlighter))
}

class GroupedTypeParamsUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedTypeParamSegments
) : GroupedContentUiModel(
  project,
  parent,
  model,
  model.segments.map {
    object : ContentSegments {
      override val segments = listOf(it)
      override val parent: Parentable = model
    }
  },
  getGroupedParamsSectionHeader(project, groupedTypeParamsSectionName, Colors.TypeParamNameBackgroundColor, model)
)