package com.intelligentComments.ui.comments.model.content.value

import com.intelligentComments.core.domain.core.ValueSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.renderers.segments.ValueSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ValueUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  value: ValueSegment,
) : ContentSegmentUiModel(project, parent, value) {
  val content = ContentSegmentsUiModel(project, this, value.content)

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(content.calculateStateHash())
  }

  override fun createRenderer(): Renderer {
    return ValueSegmentRenderer(this)
  }
}