package com.intelligentComments.ui.comments.model.content.seeAlso

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.renderers.segments.SeeAlsoSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project
import java.util.*

open class SeeAlsoUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  seeAlso: SeeAlsoSegment
) : ContentSegmentUiModel(project, parent, seeAlso) {
  companion object {
    fun getFor(project: Project, parent: UiInteractionModelBase?, seeAlso: SeeAlsoSegment): SeeAlsoUiModel {
      return when(seeAlso) {
        is SeeAlsoLinkSegment -> SeeAlsoLinkUiModel(project, parent, seeAlso)
        is SeeAlsoMemberSegment -> SeeAlsoMemberUiModel(project, parent, seeAlso)
        else -> throw IllegalArgumentException(seeAlso.javaClass.name)
      }
    }
  }

  val description = TextContentSegmentUiModel(project, this, object : TextContentSegment {
    override val highlightedText: HighlightedText = seeAlso.description
    override val parent: Parentable = seeAlso
    override val id: UUID = UUID.randomUUID()
  })

  val header = HighlightedTextUiWrapper(project, this, getFirstLevelHeader(
    project,
    seeAlsoText,
    seeAlso
  ))

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(description.calculateStateHash(), header.calculateStateHash())
  }

  override fun createRenderer(): Renderer {
    return SeeAlsoSegmentRenderer.getFor(this)
  }
}

const val seeAlsoText = "See also"

class SeeAlsoLinkUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  seeAlsoLink: SeeAlsoLinkSegment
) : SeeAlsoUiModel(project, parent, seeAlsoLink)

class SeeAlsoMemberUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  seeAlsoMember: SeeAlsoMemberSegment
) : SeeAlsoUiModel(project, parent, seeAlsoMember)