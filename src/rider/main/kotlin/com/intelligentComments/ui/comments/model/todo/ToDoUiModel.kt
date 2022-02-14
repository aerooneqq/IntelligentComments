package com.intelligentComments.ui.comments.model.todo

import com.intelligentComments.core.domain.core.ToDo
import com.intelligentComments.core.domain.core.ToDoWithTickets
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.HeaderUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.references.ReferenceUiModel
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class ToDoUiModel(
  todo: ToDo,
  parent: UiInteractionModelBase?,
  project: Project
) : UiInteractionModelBase(project, parent), ExpandableUiModel {
  companion object {
    fun getFrom(project: Project, parent: UiInteractionModelBase?, todo: ToDo): ToDoUiModel {
      return when (todo) {
        is ToDoWithTickets -> ToDoWithTicketsUiModel(todo, parent, project)
        else -> throw IllegalArgumentException(todo.toString())
      }
    }
  }

  override var isExpanded: Boolean = true

  val description = ContentSegmentsUiModel(project, this, todo.description)
  val headerUiModel =
    HeaderUiModel(project, this, todo.name, Colors.ToDoHeaderBackgroundColor, Colors.ToDoHeaderHoveredBackgroundColor)
  val blockingReferences = todo.blockingReferences.map { ReferenceUiModel(project, this, it) }


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(
      isExpanded.hashCode(),
      description.calculateStateHash(),
      headerUiModel.calculateStateHash(),
      HashUtil.calculateHashFor(blockingReferences) { it.calculateStateHash() }
    )
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}

