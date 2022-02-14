package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdHack
import com.jetbrains.rd.ide.model.RdHackContentSegment
import com.jetbrains.rd.ide.model.RdHackWithTickets

open class HackFromRd(rdHack: RdHack, project: Project) : UniqueEntityImpl(), Hack {
  companion object {
    fun getFrom(rdHack: RdHack, project: Project): HackFromRd {
      return when (rdHack) {
        is RdHackWithTickets -> HackWithTicketsFromRd(rdHack, project)
        else -> throw IllegalArgumentException(rdHack.toString())
      }
    }
  }

  final override val name: String = rdHack.name
  final override val description: ContentSegments = ContentSegmentsFromRd(rdHack.description, null, project)
  final override val blockingReferences = rdHack.blockingReferences.map { ReferenceFromRd.getFrom(project, it) }
}

class HackWithTicketsFromRd(
  rdHackWithTickets: RdHackWithTickets,
  project: Project
) : HackFromRd(rdHackWithTickets, project), HackWithTickets {
  override val tickets: Collection<Ticket> = rdHackWithTickets.tickets.map { TicketFromRd(it) }
}

class HackWithTicketsContentSegmentFromRd(
  rdHackSegment: RdHackContentSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(rdHackSegment, parent), HackWithTicketsContentSegment {
  override val hack = HackWithTicketsFromRd(rdHackSegment.hack, project)
}