package com.intelligentcomments.ui.comments.model.sections

import com.intelligentcomments.core.domain.core.ForegroundTextAnimation
import com.intelligentcomments.core.domain.core.TextHighlighter
import com.intelligentcomments.core.domain.core.TextHighlighterImpl
import com.intelligentcomments.core.domain.impl.HighlightedTextImpl
import com.intelligentcomments.ui.colors.Colors
import com.intelligentcomments.ui.comments.model.ExpandableUiModel
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentcomments.ui.core.Renderer
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project
import javax.swing.Icon

class SectionHeaderUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  val icon: Icon,
  headerTextInfo: HeaderTextInfo
) : UiInteractionModelBase(project, parent) {
  private val expandedName = headerTextInfo.expandedName
  private val collapsedName = headerTextInfo.closedName

  private val highlightedTextExpanded = HighlightedTextImpl(expandedName, null, listOf(getHeaderHighlighter(expandedName)))
  private val highlightedTextCollapsed = HighlightedTextImpl(collapsedName, null, listOf(getHeaderHighlighter(collapsedName)))

  private val expandedHeaderText = HighlightedTextUiWrapper(project, this, highlightedTextExpanded)
  private val collapsedHeaderText = HighlightedTextUiWrapper(project, this, highlightedTextCollapsed)

  val headerText: HighlightedTextUiWrapper
    get() {
      parent as ExpandableUiModel
      return if (parent.isExpanded) expandedHeaderText else collapsedHeaderText
    }

  private fun getHeaderHighlighter(text: String): TextHighlighter {
    val defaultColor = colorsProvider.getColorFor(Colors.TextDefaultColor)
    val hoveredColor = colorsProvider.getColorFor(Colors.TextDefaultHoveredColor)

    return TextHighlighterImpl(
      null,
      0,
      text.length,
      defaultColor,
      mouseInOutAnimation = ForegroundTextAnimation(hoveredColor, defaultColor)
    )
  }

  override fun dumpModel(): String = "${super.dumpModel()}::${expandedHeaderText.dumpModel()}::${collapsedHeaderText.dumpModel()}"

  override fun handleClick(e: EditorMouseEvent): Boolean {
    parent as ExpandableUiModel
    parent.isExpanded = !parent.isExpanded
    return true
  }

  override fun calculateStateHash(): Int = headerText.calculateStateHash()

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}