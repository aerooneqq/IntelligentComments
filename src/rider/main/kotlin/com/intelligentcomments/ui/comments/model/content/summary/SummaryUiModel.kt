package com.intelligentcomments.ui.comments.model.content.summary

import com.intelligentcomments.core.domain.core.SummaryContentSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.renderers.segments.SummarySegmentRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class SummaryUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  summary: SummaryContentSegment
) : ContentSegmentUiModel(project, parent) {
  val content = ContentSegmentsUiModel(project, this, summary.content)

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(content.calculateStateHash())
  }

  override fun createRenderer(): Renderer {
    return SummarySegmentRenderer(this)
  }
}