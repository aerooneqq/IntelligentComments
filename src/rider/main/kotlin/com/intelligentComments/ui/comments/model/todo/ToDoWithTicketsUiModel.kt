package com.intelligentComments.ui.comments.model.todo

import com.intelligentComments.core.domain.core.ToDoWithTickets
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.tickets.TicketUiModel
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ToDoWithTicketsUiModel(
  todo: ToDoWithTickets,
  parent: UiInteractionModelBase?,
  project: Project
) : ToDoUiModel(todo, parent, project) {
  val tickets = todo.tickets.map { TicketUiModel(it, this, project) }


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(super.hashCode(), HashUtil.calculateHashFor(tickets) { it.calculateStateHash() })
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}