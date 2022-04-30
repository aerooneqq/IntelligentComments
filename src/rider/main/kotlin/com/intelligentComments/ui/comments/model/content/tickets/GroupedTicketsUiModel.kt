package com.intelligentComments.ui.comments.model.content.tickets

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.GroupedTicketsSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.model.content.invariants.mergeSegmentsTexts
import com.intelligentComments.ui.comments.renderers.segments.tickets.GroupedTicketsSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project

private const val ticketsSectionName = "Related tickets"
class GroupedTicketsUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedTicketsSegment
) : GroupedContentUiModel(
  project,
  parent,
  model,
  listOf(
    object : ContentSegments {
      override val parent: Parentable = model
      override val segments: Collection<ContentSegment> = listOf(object : UniqueEntityImpl(), TextContentSegment {
        override val parent: Parentable = model
        override val highlightedText: HighlightedText = mergeSegmentsTexts(model.segments, this) {
          it as TicketContentSegment
          return@mergeSegmentsTexts createHighlightedTicketName(it)
        }
      })
    }
  ),
  getFirstLevelHeader(project, ticketsSectionName, model)
) {
  override fun createRenderer(): Renderer {
    return GroupedTicketsSegmentRenderer(this)
  }
}