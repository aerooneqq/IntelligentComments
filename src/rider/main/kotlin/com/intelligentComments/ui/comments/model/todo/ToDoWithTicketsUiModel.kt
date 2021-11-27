package com.intelligentComments.ui.comments.model.todo

import com.intelligentComments.core.domain.core.ToDoWithTickets
import com.intelligentComments.ui.comments.model.tickets.TicketUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ToDoWithTicketsUiModel(todo: ToDoWithTickets, project: Project) : ToDoUiModel(todo, project) {
  val tickets = todo.tickets.map { TicketUiModel(it, project) }

  override fun hashCode(): Int = (super.hashCode() * HashUtil.calculateHashFor(tickets)) % HashUtil.mod
  override fun equals(other: Any?): Boolean = other is ToDoWithTicketsUiModel && other.hashCode() == hashCode()
}