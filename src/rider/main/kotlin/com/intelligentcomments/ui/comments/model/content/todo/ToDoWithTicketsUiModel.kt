package com.intelligentcomments.ui.comments.model.content.todo

import com.intelligentcomments.core.domain.core.NameKind
import com.intelligentcomments.core.domain.core.ToDoWithTicketsContentSegment
import com.intelligentcomments.ui.comments.model.ModelWithContentSegments
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.hacks.generateContentSegmentsUiModelForNamedEntity
import com.intelligentcomments.ui.comments.renderers.ContentSegmentsRenderer
import com.intelligentcomments.ui.comments.renderers.segments.SegmentRenderer
import com.intelligentcomments.ui.core.Renderer
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