package com.intelligentcomments.ui.comments.model.content.example

import com.intelligentcomments.core.domain.core.ExampleContentSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.renderers.ContentSegmentsRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ExampleSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  exampleSegment: ExampleContentSegment
) : ContentSegmentUiModel(project, parent) {

  val content = ContentSegmentsUiModel(project, this, exampleSegment.content)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(content.calculateStateHash())
  }

  override fun createRenderer(): Renderer {
    return ContentSegmentsRenderer(content.contentSection.content)
  }
}