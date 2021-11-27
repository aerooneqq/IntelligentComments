package com.intelligentComments.ui.comments.model.hacks

import com.intelligentComments.core.domain.core.HackWithTickets
import com.intelligentComments.ui.comments.model.tickets.TicketUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class HackWithTicketsUiModel(
  hackWithTickets: HackWithTickets,
  project: Project
) : HackUiModel(hackWithTickets, project) {
  val tickets = hackWithTickets.tickets.map { TicketUiModel(it, project) }

  override fun hashCode(): Int = (super.hashCode() * HashUtil.calculateHashFor(tickets)) % HashUtil.mod
  override fun equals(other: Any?): Boolean = other is HackWithTicketsUiModel && other.hashCode() == hashCode()
}