package com.intelligentComments.ui.comments.model.todo

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.HeaderUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.references.ReferenceUiModel
import com.intelligentComments.ui.comments.model.tickets.TicketUiModel
import com.intelligentComments.ui.comments.renderers.todos.ToDoWithTicketsRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ToDoWithTicketsUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment : ToDoWithTicketsContentSegment,
) : ContentSegmentUiModel(project, parent, segment), ExpandableUiModel {
  override var isExpanded: Boolean = true

  val toDo = segment.toDo
  val description = ContentSegmentsUiModel(project, this, toDo.description)
  val headerUiModel = HeaderUiModel(project, this, toDo.name, Colors.ToDoHeaderBackgroundColor, Colors.ToDoHeaderHoveredBackgroundColor)

  val blockingReferences = toDo.blockingReferences.map {
    ReferenceUiModel(project, this, object : UniqueEntityImpl(), ReferenceContentSegment {
      override val reference: Reference = it
      override val parent: Parentable = this
    })
  }

  val tickets = toDo.tickets.map { TicketUiModel(it, this, project) }

  override fun calculateStateHash(): Int {
    val hash = HashUtil.hashCode(
      isExpanded.hashCode(),
      description.calculateStateHash(),
      headerUiModel.calculateStateHash(),
      HashUtil.calculateHashFor(blockingReferences) { it.calculateStateHash() }
    )

    return HashUtil.hashCode(hash, HashUtil.calculateHashFor(tickets) { it.calculateStateHash() })
  }

  override fun createRenderer(): Renderer = ToDoWithTicketsRenderer(this)
}