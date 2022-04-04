package com.intelligentComments.ui.comments.model.content.todo

import com.intelligentComments.core.domain.core.ToDoWithTicketsContentSegment
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.renderers.ContentSegmentsRenderer
import com.intelligentComments.ui.comments.renderers.segments.SegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ui.bindable.views.listControl.sendFocusGainedToModel

class ToDoWithTicketsUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment : ToDoWithTicketsContentSegment,
) : ContentSegmentUiModel(project, parent), ExpandableUiModel {
  override var isExpanded: Boolean = true
  val content = ContentSegmentsUiModel(project, this, segment.content.content)

  override fun calculateStateHash(): Int {
    return content.calculateStateHash()
  }

  override fun createRenderer(): Renderer = object : ContentSegmentsRenderer(content), SegmentRenderer { }
}