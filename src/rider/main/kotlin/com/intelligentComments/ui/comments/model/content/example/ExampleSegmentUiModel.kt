package com.intelligentComments.ui.comments.model.content.example

import com.intelligentComments.core.domain.core.ExampleContentSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ExampleSegmentUiModel(
  project: Project,
  exampleSegment: ExampleContentSegment
) : ContentSegmentUiModel(project, exampleSegment) {

  val content = ContentSegmentsUiModel(project, exampleSegment.content)

  override fun hashCode(): Int = HashUtil.hashCode(content.hashCode())
  override fun equals(other: Any?): Boolean = other is ExampleSegmentUiModel && other.hashCode() == hashCode()
}