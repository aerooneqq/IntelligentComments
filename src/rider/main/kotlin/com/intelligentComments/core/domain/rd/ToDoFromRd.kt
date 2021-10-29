package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdToDo
import com.jetbrains.rd.ide.model.RdToDoWithTickets

open class ToDoFromRd(todo: RdToDo, project: Project) : UniqueEntityImpl(), ToDo {
    companion object {
        fun getFrom(todo: RdToDo, project: Project): ToDoFromRd {
            return when(todo) {
                is RdToDoWithTickets -> ToDoWithTicketsFromRd(todo, project)
                else -> throw IllegalArgumentException(todo.toString())
            }
        }
    }

    final override val author: CommentAuthor = AuthorFromRd(todo.author)
    final override val name: String = todo.name
    final override val description: ContentSegments = ContentSegmentsFromRd(todo.description, project)
    final override val blockingReferences: Collection<Reference> = todo.blockingReferences.map { ReferenceFromRd.getFrom(it) }
}

class ToDoWithTicketsFromRd(todo: RdToDoWithTickets, project: Project) : ToDoFromRd(todo, project), ToDoWithTickets {
    override val tickets: Collection<Ticket> = todo.tickets.map { TicketFromRd(it) }
}