package com.intelligentComments.ui.comments.model.content.todo

import com.intelligentComments.core.domain.core.NameKind
import com.intelligentComments.core.domain.core.ToDoWithTicketsContentSegment
import com.intelligentComments.ui.comments.model.ModelWithContentSegments
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.hacks.generateContentSegmentsUiModelForNamedEntity
import com.intelligentComments.ui.comments.renderers.ContentSegmentsRenderer
import com.intelligentComments.ui.comments.renderers.segments.SegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project

class ToDoWithTicketsUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment : ToDoWithTicketsContentSegment,
) : ContentSegmentUiModel(project, parent), ModelWithContentSegments {
  override val content = generateContentSegmentsUiModelForNamedEntity(NameKind.Todo, segment, project, this)

  override fun calculateStateHash(): Int {
    return content.calculateStateHash()
  }

  override fun createRenderer(): Renderer = object : ContentSegmentsRenderer(content), SegmentRenderer { }
}