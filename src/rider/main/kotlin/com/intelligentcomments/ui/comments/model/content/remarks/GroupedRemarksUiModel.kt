package com.intelligentcomments.ui.comments.model.content.remarks

import com.intelligentcomments.core.domain.impl.GroupedRemarksSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentcomments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentcomments.ui.comments.renderers.segments.GroupedRemarksRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intellij.openapi.project.Project

private const val remarksSectionName = "Remarks"

class GroupedRemarksUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedRemarksSegment
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