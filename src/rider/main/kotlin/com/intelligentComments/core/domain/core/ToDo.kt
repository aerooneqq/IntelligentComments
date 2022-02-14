package com.intelligentComments.core.domain.core

interface ToDo : UniqueEntity, EntityBlockedByReferences {
  val author: CommentAuthor
  val name: String
  val description: ContentSegments
}

interface ToDoWithTickets : ToDo, EntityWithAssociatedTickets

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

interface ToDoContentSegment : ContentSegment {
  val toDo: ToDo
}