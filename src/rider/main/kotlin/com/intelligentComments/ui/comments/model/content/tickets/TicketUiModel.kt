package com.intelligentComments.ui.comments.model.content.tickets

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.renderers.segments.tickets.TicketSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TicketUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  private val ticket: TicketContentSegment
) : ContentSegmentUiModel(project, parent) {
  val description = ContentSegmentsUiModel(project, this, ticket.description.content)
  val displayName: HighlightedTextUiWrapper


  init {
    displayName = HighlightedTextUiWrapper(project, this, createHighlightedTicketName(ticket))
  }


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(description.calculateStateHash())
  }

  override fun createRenderer(): Renderer = TicketSegmentRenderer(this)
}

fun createHighlightedTicketName(ticket: TicketContentSegment): HighlightedText {
  val text = ticket.reference.rawValue
  val highlighter = CommonsHighlightersFactory.tryCreateCommentHighlighter(
    ticket, text.length, references = listOf(ticket.reference), animation = UnderlineTextAnimation())

  return HighlightedTextImpl(text, ticket, highlighter)
}