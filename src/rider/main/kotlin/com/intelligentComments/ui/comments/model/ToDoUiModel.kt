package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.ToDo
import com.intelligentComments.core.domain.core.ToDoWithTickets
import com.intelligentComments.ui.colors.Colors
import com.intellij.openapi.project.Project

open class ToDoUiModel(todo: ToDo, project: Project) : UiInteractionModelBase(project), ExpandableUiModel {
    companion object {
        fun getFrom(project: Project, todo: ToDo): ToDoUiModel {
            return when(todo) {
                is ToDoWithTickets -> ToDoWithTicketsUiModel(todo, project)
                else -> throw IllegalArgumentException(todo.toString())
            }
        }
    }

    override var isExpanded: Boolean = true

    val name = todo.name
    val description = ContentSegmentsUiModel(project, todo.description)
    val headerUiModel = HeaderUiModel(project, this, todo.name, Colors.ReferenceHeaderBackgroundColor, Colors.ReferenceHeaderHoveredBackgroundColor)
}

class ToDoWithTicketsUiModel(todo: ToDoWithTickets, project: Project) : ToDoUiModel(todo, project) {
}