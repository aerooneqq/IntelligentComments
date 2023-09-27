package com.intelligentcomments.ui.comments.model.content.seeAlso

import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentcomments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project
import java.util.*

open class SeeAlsoUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  seeAlso: SeeAlsoSegment
) : ContentSegmentUiModel(project, parent) {
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

  override fun dumpModel() = "${super.dumpModel()}::${description.dumpModel()}::${header.dumpModel()}"
  override fun calculateStateHash() = HashUtil.hashCode(description.calculateStateHash(), header.calculateStateHash())
  override fun createRenderer() = LeftTextHeaderAndRightContentRenderer(header, listOf(description))
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