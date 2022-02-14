package com.intelligentComments.ui.comments.model.content.remarks

import com.intelligentComments.core.domain.core.RemarksSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.renderers.segments.RemarksSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class RemarksUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  remarksSegment: RemarksSegment
) : ContentSegmentUiModel(project, parent, remarksSegment) {
  val content = ContentSegmentsUiModel(project, this, remarksSegment.content)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(content.calculateStateHash())
  }

  override fun createRenderer(): Renderer {
    return RemarksSegmentRenderer(this)
  }
}