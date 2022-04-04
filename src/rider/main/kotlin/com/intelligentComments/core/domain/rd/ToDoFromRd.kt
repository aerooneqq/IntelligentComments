package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.ContentProcessingStrategyImpl
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdToDoContentSegment

class ToDoContentSegmentFromRd(
  contentSegment: RdToDoContentSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(contentSegment, parent), ToDoWithTicketsContentSegment {
  override val content: EntityWithContentSegments = EntityWithContentSegmentsFromRd(contentSegment.content, this, project)
  override val blockingReferences: Collection<Reference> = emptyList()
  override val tickets: Collection<TicketContentSegment> = emptyList()

  init {
    content.content.processSegments(project.service<ContentProcessingStrategyImpl>())
  }
}