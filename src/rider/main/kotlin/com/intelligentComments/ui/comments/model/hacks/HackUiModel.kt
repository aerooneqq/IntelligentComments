package com.intelligentComments.ui.comments.model.hacks

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.HeaderUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.references.ReferenceUiModel
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class HackUiModel(
  segment: HackContentSegment,
  parent: UiInteractionModelBase?,
  project: Project
) : ContentSegmentUiModel(project, parent, segment), ExpandableUiModel {
  companion object {
    fun getFrom(project: Project, parent: UiInteractionModelBase?, segment: HackContentSegment): HackUiModel {
      return when (val hack = segment.hack) {
        is HackWithTickets -> HackWithTicketsUiModel(segment, hack, parent, project)
        else -> throw IllegalArgumentException(hack.toString())
      }
    }
  }

  val hack = segment.hack
  val description = ContentSegmentsUiModel(project, this, hack.description)
  val blockingReferences = hack.blockingReferences.map {
    ReferenceUiModel(project, this, object : UniqueEntityImpl(), ReferenceContentSegment {
      override val reference: Reference = it
      override val parent: Parentable = this
    })
  }

  val headerUiModel = HeaderUiModel(project, this, hack.name, Colors.HackHeaderBackgroundColor, Colors.HackHeaderHoveredBackgroundColor)
  override var isExpanded: Boolean = true


  override fun calculateStateHash(): Int {
    val referencesHash = HashUtil.calculateHashFor(blockingReferences) { it.calculateStateHash() }
    return HashUtil.hashCode(description.calculateStateHash(), referencesHash, headerUiModel.calculateStateHash())
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}