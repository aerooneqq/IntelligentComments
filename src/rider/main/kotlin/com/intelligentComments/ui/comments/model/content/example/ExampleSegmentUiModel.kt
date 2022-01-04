package com.intelligentComments.ui.comments.model.content.example

import com.intelligentComments.core.domain.core.ExampleContentSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ExampleSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  exampleSegment: ExampleContentSegment
) : ContentSegmentUiModel(project, parent, exampleSegment) {

  val content = ContentSegmentsUiModel(project, this, exampleSegment.content)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(content.calculateStateHash())
  }
}