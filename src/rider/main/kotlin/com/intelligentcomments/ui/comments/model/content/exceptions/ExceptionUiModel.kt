package com.intelligentcomments.ui.comments.model.content.exceptions

import com.intelligentcomments.core.domain.core.ExceptionSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ExceptionUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  exceptionSegment: ExceptionSegment
) : ContentSegmentUiModel(project, parent) {
  val name = HighlightedTextUiWrapper(project, this, exceptionSegment.name)
  val content = ContentSegmentsUiModel(project, this, exceptionSegment.content)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(name.calculateStateHash(), content.calculateStateHash())
  }

  override fun createRenderer(): Renderer {
    return LeftTextHeaderAndRightContentRenderer(name, content.contentSection.content)
  }
}