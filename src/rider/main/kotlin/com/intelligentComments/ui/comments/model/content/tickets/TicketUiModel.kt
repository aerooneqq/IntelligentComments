package com.intelligentComments.ui.comments.model.content.tickets

import com.intelligentComments.core.domain.core.ForegroundTextAnimation
import com.intelligentComments.core.domain.core.TextHighlighter
import com.intelligentComments.core.domain.core.TextHighlighterImpl
import com.intelligentComments.core.domain.core.Ticket
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

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

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}