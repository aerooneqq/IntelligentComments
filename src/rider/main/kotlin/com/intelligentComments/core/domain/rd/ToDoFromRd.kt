package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdToDo
import com.jetbrains.rd.ide.model.RdToDoContentSegment
import com.jetbrains.rd.ide.model.RdToDoWithTickets

open class ToDoFromRd(todo: RdToDo, project: Project) : UniqueEntityImpl(), ToDo {
  final override val name: String = todo.text.text
  final override val blockingReferences: Collection<Reference> =
    todo.blockingReferences.map { ReferenceFromRd.getFrom(project, it) }
}

class ToDoWithTicketsFromRd(todo: RdToDoWithTickets, project: Project) : ToDoFromRd(todo, project), ToDoWithTickets {
  override val tickets: Collection<Ticket> = todo.tickets.map { TicketFromRd(it) }
}

class ToDoContentSegmentFromRd(
  contentSegment: RdToDoContentSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(contentSegment, parent), ToDoWithTicketsContentSegment {
  override val toDo = ToDoWithTicketsFromRd(contentSegment.toDo, project)
}