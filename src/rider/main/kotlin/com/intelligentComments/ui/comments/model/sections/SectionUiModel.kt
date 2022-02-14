package com.intelligentComments.ui.comments.model.sections

import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.renderers.segments.DefaultSegmentsRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class SectionUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  val content: Collection<ContentSegmentUiModel>
) : UiInteractionModelBase(project, parent) {

  override fun calculateStateHash(): Int {
    return HashUtil.calculateHashFor(content) { it.calculateStateHash() }
  }

  override fun createRenderer(): Renderer {
    return DefaultSegmentsRenderer(this)
  }
}