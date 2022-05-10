package com.intelligentcomments.ui.comments.model.content.todo

import com.intelligentcomments.core.domain.core.NameKind
import com.intelligentcomments.core.domain.impl.GroupedTodosSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentcomments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentcomments.ui.comments.model.content.hacks.generateContentSegmentsForNamedEntity
import com.intelligentcomments.ui.comments.renderers.segments.GroupedTodosRenderer
import com.intelligentcomments.ui.core.Renderer
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