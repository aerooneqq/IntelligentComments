package com.intelligentComments.ui.comments.model.sections

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project
import java.awt.Color
import javax.swing.Icon

class SectionHeaderUiModel(
  project: Project,
  val icon: Icon,
  headerTextInfo: HeaderTextInfo,
  private val parent: ExpandableUiModel
) : UiInteractionModelBase(project) {
  private val expandedName = headerTextInfo.expandedName
  private val collapsedName = headerTextInfo.closedName

  private val highlightedTextExpanded = HighlightedTextImpl(expandedName, listOf(getHeaderHighlighter(expandedName)))
  private val highlightedTextCollapsed = HighlightedTextImpl(collapsedName, listOf(getHeaderHighlighter(collapsedName)))

  private val expandedHeaderText = HighlightedTextUiWrapper(project, highlightedTextExpanded)
  private val collapsedHeaderText = HighlightedTextUiWrapper(project, highlightedTextCollapsed)

  val headerText: HighlightedTextUiWrapper
    get() = if (parent.isExpanded) expandedHeaderText else collapsedHeaderText

  private fun getHeaderHighlighter(text: String): TextHighlighter {
    val defaultColor = colorsProvider.getColorFor(Colors.TextDefaultColor)
    val hoveredColor = colorsProvider.getColorFor(Colors.TextDefaultHoveredColor)

    return TextHighlighterImpl(
      0,
      text.length,
      defaultColor,
      mouseInOutAnimation = ForegroundTextAnimation(hoveredColor, defaultColor)
    )
  }

  override fun handleClick(e: EditorMouseEvent): Boolean {
    parent.isExpanded = !parent.isExpanded
    return true
  }
}