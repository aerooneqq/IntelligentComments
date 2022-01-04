package com.intelligentComments.ui.comments.model.highlighters

import com.intelligentComments.core.domain.core.HighlightedText
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class HighlightedTextUiWrapper(
  project: Project,
  parent: UiInteractionModelBase?,
  highlightedText: HighlightedText
) : UiInteractionModelBase(project, parent) {
  val text = highlightedText.text
  val highlighters = highlightedText.highlighters.map { HighlighterUiModel.getFor(project, this, it) }


  override fun calculateStateHash(): Int {
    var textHashCode = text.hashCode()
    if (textHashCode == 0) {
      textHashCode = 1
    }

    return HashUtil.hashCode(textHashCode, HashUtil.calculateHashFor(highlighters) { it.calculateStateHash() })
  }
}