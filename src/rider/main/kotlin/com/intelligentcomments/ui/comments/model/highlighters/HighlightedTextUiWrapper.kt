package com.intelligentcomments.ui.comments.model.highlighters

import com.intelligentcomments.core.domain.core.HighlightedText
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class HighlightedTextUiWrapper(
  project: Project,
  parent: UiInteractionModelBase?,
  highlightedText: HighlightedText
) : UiInteractionModelBase(project, parent) {
  val text = highlightedText.text
  val highlighters = highlightedText.highlighters.map { HighlighterUiModel.getFor(project, this, it) }


  override fun dumpModel(): String = "${text}[${highlighters.joinToString(",") { it.dumpModel() }}]"

  override fun calculateStateHash(): Int {
    var textHashCode = text.hashCode()
    if (textHashCode == 0) {
      textHashCode = 1
    }

    return HashUtil.hashCode(textHashCode, HashUtil.calculateHashFor(highlighters) { it.calculateStateHash() })
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}