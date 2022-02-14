package com.intelligentComments.core.domain.core

interface Hack : UniqueEntity, EntityBlockedByReferences {
  val name: String
  val description: ContentSegments
}

interface HackWithTickets : Hack, EntityWithAssociatedTickets

interface HackContentSegment : ContentSegment {
  val hack: Hack
}