package com.intelligentComments.ui.comments.model.todo

import com.intelligentComments.core.domain.core.ToDo
import com.intelligentComments.core.domain.core.ToDoWithTickets
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.HeaderUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.util.HashUtil
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

    override fun hashCode(): Int = (isExpanded.hashCode() * name.hashCode() * description.hashCode() * headerUiModel.hashCode()) % HashUtil.mod
    override fun equals(other: Any?): Boolean = other is ToDoUiModel && other.hashCode() == hashCode()
}

