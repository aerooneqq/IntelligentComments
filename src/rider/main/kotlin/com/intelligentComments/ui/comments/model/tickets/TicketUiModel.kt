package com.intelligentComments.ui.comments.model.tickets

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project
import java.awt.Color

class TicketUiModel(private val ticket: Ticket, project: Project) : UiInteractionModelBase(project) {
  val nameText = HighlightedTextUiWrapper(project, HighlightedTextImpl(ticket.shortName, null, listOf(getNameHighlighter())))
  val url = ticket.url

  private fun getNameHighlighter(): TextHighlighter {
    val defaultColor = colorsProvider.getColorFor(Colors.TextUrlColor)
    val hoveredColor = colorsProvider.getColorFor(Colors.TextUrlColorHovered)

    return TextHighlighterImpl(
      0,
      ticket.shortName.length,
      defaultColor,
      mouseInOutAnimation = ForegroundTextAnimation(hoveredColor, defaultColor)
    )
  }

  override fun hashCode(): Int = HashUtil.hashCode(ticket.shortName.hashCode(), url.hashCode())
  override fun equals(other: Any?): Boolean = other is TicketUiModel && other.hashCode() == hashCode()
}