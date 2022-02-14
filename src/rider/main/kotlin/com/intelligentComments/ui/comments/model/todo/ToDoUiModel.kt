package com.intelligentComments.ui.comments.model.todo

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.HeaderUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.references.ReferenceUiModel
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class ToDoUiModel(
  contentSegment: ToDoContentSegment,
  parent: UiInteractionModelBase?,
  project: Project
) : ContentSegmentUiModel(project, parent, contentSegment), ExpandableUiModel {
  companion object {
    fun getFrom(project: Project, parent: UiInteractionModelBase?, segment: ToDoContentSegment): ToDoUiModel {
      return when (val todo = segment.toDo) {
        is ToDoWithTickets -> ToDoWithTicketsUiModel(segment, todo, parent, project)
        else -> throw IllegalArgumentException(todo.toString())
      }
    }
  }

  override var isExpanded: Boolean = true

  val toDo = contentSegment.toDo
  val description = ContentSegmentsUiModel(project, this, toDo.description)
  val headerUiModel =
    HeaderUiModel(project, this, toDo.name, Colors.ToDoHeaderBackgroundColor, Colors.ToDoHeaderHoveredBackgroundColor)

  val blockingReferences = toDo.blockingReferences.map {
    ReferenceUiModel(project, this, object : UniqueEntityImpl(), ReferenceContentSegment {
      override val reference: Reference = it
      override val parent: Parentable = this
    })
  }

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

