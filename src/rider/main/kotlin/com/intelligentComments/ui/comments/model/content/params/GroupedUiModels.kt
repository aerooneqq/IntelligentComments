package com.intelligentComments.ui.comments.model.content.params

import com.intelligentComments.core.domain.core.ContentSegments
import com.intelligentComments.core.domain.core.Parentable
import com.intelligentComments.core.domain.impl.GroupedParamSegment
import com.intelligentComments.core.domain.impl.GroupedTypeParamSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.renderers.segments.GroupedParamsRenderer
import com.intelligentComments.ui.comments.renderers.segments.GroupedTypeParamsRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project

class GroupedParamsUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedParamSegment
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
  getFirstLevelHeader(project, groupedParamsSectionName, model)
) {
  override fun createRenderer(): Renderer {
    return GroupedParamsRenderer(this)
  }
}

private const val groupedParamsSectionName = "Parameters"
private const val groupedTypeParamsSectionName = "Type parameters"

class GroupedTypeParamsUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedTypeParamSegment
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
  getFirstLevelHeader(project, groupedTypeParamsSectionName, model)
) {
  override fun createRenderer(): Renderer {
    return GroupedTypeParamsRenderer(this)
  }
}