package com.intelligentcomments.ui.comments.model.content.tickets

import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.core.domain.impl.FrontendTicketReferenceImpl
import com.intelligentcomments.core.domain.impl.HighlightedTextImpl
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
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

  override fun createRenderer(): Renderer = LeftTextHeaderAndRightContentRenderer(displayName, description)
}

fun createHighlightedTicketName(ticket: TicketContentSegment): HighlightedText {
  val reference = ticket.reference
  val text = if (reference is HttpLinkReference) {
    reference.displayName
  } else {
    reference.rawValue
  }

  val highlighter = CommonsHighlightersFactory.tryCreateCommentHighlighter(
    ticket,
    text.length,
    references = listOf(ticket.reference, FrontendTicketReferenceImpl("Ticket", ticket)),
    animation = UnderlineTextAnimation()
  )

  return HighlightedTextImpl(text, ticket, highlighter)
}