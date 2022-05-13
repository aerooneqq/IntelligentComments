package com.intelligentcomments.ui.comments.model.content.params

import com.intelligentcomments.core.domain.core.ContentSegments
import com.intelligentcomments.core.domain.core.Parentable
import com.intelligentcomments.core.domain.impl.GroupedParamSegment
import com.intelligentcomments.core.domain.impl.GroupedTypeParamSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentcomments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentcomments.ui.comments.renderers.segments.GroupedTypeParamsRenderer
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer
import com.intelligentcomments.ui.core.Renderer
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
    return LeftTextHeaderAndRightContentRenderer(header, content)
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
    return LeftTextHeaderAndRightContentRenderer(header, content)
  }
}