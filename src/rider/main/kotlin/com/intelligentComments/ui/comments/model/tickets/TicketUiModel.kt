package com.intelligentComments.ui.comments.model.tickets

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project
import java.awt.Color

class TicketUiModel(
  private val ticket: Ticket,
  parent: UiInteractionModelBase?,
  project: Project
) : UiInteractionModelBase(project, parent) {
  val nameText = HighlightedTextUiWrapper(project, this, HighlightedTextImpl(ticket.shortName, null, listOf(getNameHighlighter())))
  val url = ticket.url

  private fun getNameHighlighter(): TextHighlighter {
    val defaultColor = colorsProvider.getColorFor(Colors.TextUrlColor)
    val hoveredColor = colorsProvider.getColorFor(Colors.TextUrlColorHovered)

    return TextHighlighterImpl(
      null,
      0,
      ticket.shortName.length,
      defaultColor,
      mouseInOutAnimation = ForegroundTextAnimation(hoveredColor, defaultColor)
    )
  }


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(ticket.shortName.hashCode(), url.hashCode())
  }
}