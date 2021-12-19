package com.intelligentComments.ui.comments.model.content.seeAlso

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intellij.openapi.project.Project
import java.awt.Color
import java.util.*

open class SeeAlsoUiModel(project: Project, seeAlso: SeeAlsoSegment) : ContentSegmentUiModel(project, seeAlso) {
  companion object {
    const val seeAlsoText = "See also:"

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

  protected open val headerText = HighlightedTextImpl(seeAlsoText, listOf(getHeaderHighlighter()))

  private fun getHeaderHighlighter(): TextHighlighter {
    val textColor = colorsProvider.getColorFor(Colors.TextDefaultColor)
    val backgroundColor = colorsProvider.getColorFor(Colors.SeeAlsoBackgroundColor)

    return CommonsHighlightersFactory.getWithRoundedBackgroundRect(textColor, backgroundColor, seeAlsoText.length)
  }

  val header = HighlightedTextUiWrapper(project, headerText)
}

class SeeAlsoLinkUiModel(
  project: Project,
  seeAlsoLink: SeeAlsoLinkSegment
) : SeeAlsoUiModel(project, seeAlsoLink)

class SeeAlsoMemberUiModel(
  project: Project,
  seeAlsoMember: SeeAlsoMemberSegment
) : SeeAlsoUiModel(project, seeAlsoMember)