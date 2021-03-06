package com.intelligentcomments.ui.comments.model.content.summary

import com.intelligentcomments.core.domain.core.SummaryContentSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.renderers.ContentSegmentsRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class SummaryUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  summary: SummaryContentSegment
) : ContentSegmentUiModel(project, parent) {
  val content = ContentSegmentsUiModel(project, this, summary.content)

  override fun dumpModel() = "${super.dumpModel()}: \n{\n${content.dumpModel()}\n}"
  override fun calculateStateHash(): Int = HashUtil.hashCode(content.calculateStateHash())
  override fun createRenderer(): Renderer = ContentSegmentsRenderer(content)
}