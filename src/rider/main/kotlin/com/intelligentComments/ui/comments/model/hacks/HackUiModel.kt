package com.intelligentComments.ui.comments.model.hacks

import com.intelligentComments.core.domain.core.Hack
import com.intelligentComments.core.domain.core.HackWithTickets
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.HeaderUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.references.ReferenceUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class HackUiModel(
  hack: Hack,
  parent: UiInteractionModelBase?,
  project: Project
) : UiInteractionModelBase(project, parent), ExpandableUiModel {
  companion object {
    fun getFrom(project: Project, parent: UiInteractionModelBase?, hack: Hack): HackUiModel {
      return when (hack) {
        is HackWithTickets -> HackWithTicketsUiModel(hack, parent, project)
        else -> throw IllegalArgumentException(hack.toString())
      }
    }
  }

  val description = ContentSegmentsUiModel(project, this, hack.description)
  val blockingReferences = hack.blockingReferences.map { ReferenceUiModel(project, this, it) }
  val headerUiModel =
    HeaderUiModel(project, this, hack.name, Colors.HackHeaderBackgroundColor, Colors.HackHeaderHoveredBackgroundColor)
  override var isExpanded: Boolean = true


  override fun calculateStateHash(): Int {
    val referencesHash = HashUtil.calculateHashFor(blockingReferences) { it.calculateStateHash() }
    return HashUtil.hashCode(description.calculateStateHash(), referencesHash, headerUiModel.calculateStateHash())
  }
}