package com.intelligentComments.core.domain.core

import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.tickets.TicketUiModel
import com.intelligentComments.ui.comments.model.content.todo.ToDoWithTicketsUiModel
import com.intellij.openapi.project.Project


interface TicketContentSegment : ContentSegment {
  val reference: Reference
  val description: EntityWithContentSegments

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return TicketUiModel(project, parent, this)
  }
}

interface ToDoWithTicketsContentSegment : ContentSegmentWithOptionalName, ContentSegmentWithInnerContent {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return ToDoWithTicketsUiModel(project, parent, this)
  }
}