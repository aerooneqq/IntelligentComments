package com.intelligentComments.ui.comments.model.hacks

import com.intelligentComments.core.domain.core.HackWithTickets
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.tickets.TicketUiModel
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class HackWithTicketsUiModel(
  hackWithTickets: HackWithTickets,
  parent: UiInteractionModelBase?,
  project: Project
) : HackUiModel(hackWithTickets, parent, project) {
  val tickets = hackWithTickets.tickets.map { TicketUiModel(it, this, project) }

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(super.hashCode(), HashUtil.calculateHashFor(tickets) { it.calculateStateHash() })
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}