package com.intelligentComments.core.domain.core


interface EntityWithAssociatedTickets {
  val tickets: Collection<TicketContentSegment>
}

interface EntityBlockedByReferences {
  val blockingReferences: Collection<Reference>
}

interface TicketContentSegment : ContentSegment {
  val reference: Reference
  val description: EntityWithContentSegments
}

interface ToDoWithTicketsContentSegment : ContentSegment, EntityBlockedByReferences, EntityWithAssociatedTickets {
  val content: EntityWithContentSegments
}