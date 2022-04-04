package com.intelligentComments.ui.comments.model.hacks

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.HeaderUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.content.references.ReferenceUiModel
import com.intelligentComments.ui.comments.model.content.tickets.TicketUiModel
import com.intelligentComments.ui.comments.renderers.hacks.HackWithTicketsRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class HackWithTicketsUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment: HackWithTicketsContentSegment,
) : ContentSegmentUiModel(project, parent), ExpandableUiModel {
  private val hack = segment.hack

  val description = ContentSegmentsUiModel(project, this, hack.description)
  val blockingReferences = hack.blockingReferences.map {
    ReferenceUiModel(project, this, object : UniqueEntityImpl(), ReferenceContentSegment {
      override val reference: Reference = it
      override val parent: Parentable = this
      override val description: EntityWithContentSegments = createEmptyEntityWithContentSegments()
      override val name: HighlightedText = HighlightedTextImpl(it.rawValue, this)
    })
  }

  val headerUiModel = HeaderUiModel(project, this, hack.name, Colors.HackHeaderBackgroundColor, Colors.HackHeaderHoveredBackgroundColor)
  val tickets = segment.hack.tickets.map { TicketUiModel(project, this, it) }

  override var isExpanded: Boolean = true
  override fun calculateStateHash(): Int {
    val referencesHash = HashUtil.calculateHashFor(blockingReferences) { it.calculateStateHash() }
    val hash = HashUtil.hashCode(description.calculateStateHash(), referencesHash, headerUiModel.calculateStateHash())
    return HashUtil.hashCode(hash, HashUtil.calculateHashFor(tickets) { it.calculateStateHash() })
   }

  override fun createRenderer(): Renderer = HackWithTicketsRenderer(this)
}