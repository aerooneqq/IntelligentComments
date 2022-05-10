package com.intelligentcomments.ui.comments.model.content.summary

import com.intelligentcomments.core.domain.core.GroupedContentSegment
import com.intelligentcomments.core.domain.core.SummaryContentSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentcomments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentcomments.ui.comments.renderers.segments.GroupedSummariesRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intellij.openapi.project.Project

class GroupedSummaryUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedContentSegment<SummaryContentSegment>
) : GroupedContentUiModel(
  project,
  parent,
  model,
  model.segments.map { it.content },
  getFirstLevelHeader(
    project,
    summarySectionName,
    model
  )
) {
  override fun createRenderer(): Renderer {
    return GroupedSummariesRenderer(this)
  }
}

private const val summarySectionName = "Summary"