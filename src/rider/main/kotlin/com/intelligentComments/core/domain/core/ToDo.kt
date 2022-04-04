package com.intelligentComments.core.domain.core

interface ToDo : UniqueEntity, EntityBlockedByReferences {
  val content: EntityWithContentSegments
}

interface EntityWithAssociatedTickets {
  val tickets: Collection<Ticket>
}

interface EntityBlockedByReferences {
  val blockingReferences: Collection<Reference>
}

interface Ticket : UniqueEntity {
  val url: String
  val shortName: String
}

interface ToDoWithTicketsContentSegment : ContentSegment, EntityBlockedByReferences, EntityWithAssociatedTickets {
  val content: EntityWithContentSegments
}