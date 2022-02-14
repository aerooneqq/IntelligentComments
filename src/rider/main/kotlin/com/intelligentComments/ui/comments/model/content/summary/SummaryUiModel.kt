package com.intelligentComments.ui.comments.model.content.summary

import com.intelligentComments.core.domain.core.SummaryContentSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.renderers.segments.SummarySegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class SummaryUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  summary: SummaryContentSegment
) : ContentSegmentUiModel(project, parent, summary) {
  val content = ContentSegmentsUiModel(project, this, summary.content)

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(content.calculateStateHash())
  }

  override fun createRenderer(): Renderer {
    return SummarySegmentRenderer(this)
  }
}