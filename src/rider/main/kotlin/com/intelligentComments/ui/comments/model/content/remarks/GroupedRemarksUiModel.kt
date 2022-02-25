package com.intelligentComments.ui.comments.model.content.remarks

import com.intelligentComments.core.domain.impl.GroupedRemarksSegments
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.renderers.segments.GroupedRemarksRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project

private const val remarksSectionName = "Remarks"

class GroupedRemarksUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedRemarksSegments
) : GroupedContentUiModel(
  project,
  parent,
  model,
  model.segments.map { it.content },
  getFirstLevelHeader(
    project,
    remarksSectionName,
    model
  )
) {
  override fun createRenderer(): Renderer {
    return GroupedRemarksRenderer(this)
  }
}