package com.intelligentcomments.ui.comments.model.content.remarks

import com.intelligentcomments.core.domain.core.RemarksSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.renderers.ContentSegmentsRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class RemarksUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  remarksSegment: RemarksSegment
) : ContentSegmentUiModel(project, parent) {
  val content = ContentSegmentsUiModel(project, this, remarksSegment.content)


  override fun dumpModel(): String = "${super.dumpModel()}: \n{\n${content.dumpModel()}\n}"
  override fun calculateStateHash() = HashUtil.hashCode(content.calculateStateHash())
  override fun createRenderer() = ContentSegmentsRenderer(content)
}