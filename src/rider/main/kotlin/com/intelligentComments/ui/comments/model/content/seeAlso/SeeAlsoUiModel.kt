package com.intelligentComments.ui.comments.model.content.seeAlso

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project
import java.util.*

open class SeeAlsoUiModel(
  project: Project,
  seeAlso: SeeAlsoSegment
) : ContentSegmentUiModel(project, seeAlso) {
  companion object {
    fun getFor(project: Project, seeAlso: SeeAlsoSegment): SeeAlsoUiModel {
      return when(seeAlso) {
        is SeeAlsoLinkSegment -> SeeAlsoLinkUiModel(project, seeAlso)
        is SeeAlsoMemberSegment -> SeeAlsoMemberUiModel(project, seeAlso)
        else -> throw IllegalArgumentException(seeAlso.javaClass.name)
      }
    }
  }

  val description = TextContentSegmentUiModel(project, object : TextContentSegment {
    override val highlightedText: HighlightedText = seeAlso.description
    override val id: UUID = UUID.randomUUID()
  })

  val header = HighlightedTextUiWrapper(project, getFirstLevelHeader(
    project,
    seeAlsoText,
    Colors.TextInSectionsRectanglesHeadersColor,
    Colors.SeeAlsoBackgroundColor
  ))

  override fun hashCode(): Int = HashUtil.hashCode(description.hashCode(), header.hashCode())

  override fun equals(other: Any?): Boolean = other is SeeAlsoUiModel && hashCode() == other.hashCode()
}

const val seeAlsoText = "See also"

class SeeAlsoLinkUiModel(project: Project, seeAlsoLink: SeeAlsoLinkSegment) : SeeAlsoUiModel(project, seeAlsoLink)
class SeeAlsoMemberUiModel(project: Project, seeAlsoMember: SeeAlsoMemberSegment) : SeeAlsoUiModel(project, seeAlsoMember)