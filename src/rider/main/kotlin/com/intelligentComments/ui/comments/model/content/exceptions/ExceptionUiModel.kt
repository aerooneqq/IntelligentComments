package com.intelligentComments.ui.comments.model.content.exceptions

import com.intelligentComments.core.domain.core.ExceptionSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.renderers.segments.ExceptionSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ExceptionUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  exceptionSegment: ExceptionSegment
) : ContentSegmentUiModel(project, parent, exceptionSegment) {
  val name = HighlightedTextUiWrapper(project, this, exceptionSegment.name)
  val content = ContentSegmentsUiModel(project, this, exceptionSegment.content)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(name.calculateStateHash(), content.calculateStateHash())
  }

  override fun createRenderer(): Renderer {
    return ExceptionSegmentRenderer(this)
  }
}