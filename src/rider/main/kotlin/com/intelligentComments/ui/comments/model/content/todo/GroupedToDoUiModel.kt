package com.intelligentComments.ui.comments.model.content.todo

import com.intelligentComments.core.domain.core.NameKind
import com.intelligentComments.core.domain.impl.GroupedTodosSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.model.content.hacks.generateContentSegmentsForNamedEntity
import com.intelligentComments.ui.comments.renderers.segments.GroupedTodosRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project


private const val todoSectionsName = "Todos"
class GroupedToDoUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment: GroupedTodosSegment
) : GroupedContentUiModel(
  project,
  parent,
  segment,
  segment.segments.map { generateContentSegmentsForNamedEntity(NameKind.Todo, it) },
  getFirstLevelHeader(
    project,
    todoSectionsName,
    segment
  )
) {
  override fun createRenderer(): Renderer {
    return GroupedTodosRenderer(this)
  }
}