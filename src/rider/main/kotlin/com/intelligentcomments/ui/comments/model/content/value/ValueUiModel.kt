package com.intelligentcomments.ui.comments.model.content.value

import com.intelligentcomments.core.domain.core.ValueSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.renderers.ContentSegmentsRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ValueUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  value: ValueSegment,
) : ContentSegmentUiModel(project, parent) {
  val content = ContentSegmentsUiModel(project, this, value.content)

  override fun dumpModel(): String = "${super.dumpModel()}: \n{\n${content.dumpModel()}\n}"
  override fun calculateStateHash(): Int = HashUtil.hashCode(content.calculateStateHash())
  override fun createRenderer(): Renderer = ContentSegmentsRenderer(content)
}