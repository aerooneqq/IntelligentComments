package com.intelligentcomments.ui.comments.model.content.tickets

import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.core.domain.impl.GroupedTicketsSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentcomments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentcomments.ui.comments.model.content.invariants.mergeSegmentsTexts
import com.intelligentcomments.ui.comments.renderers.segments.tickets.GroupedTicketsSegmentRenderer
import com.intelligentcomments.ui.core.Renderer
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