package com.intelligentComments.ui.comments.model.highlighters

import com.intelligentComments.core.domain.core.HighlightedText
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class HighlightedTextUiWrapper(project: Project, highlightedText: HighlightedText) {
  val text = highlightedText.text
  val highlighters = highlightedText.highlighters.map { HighlighterUiModel.getFor(project, it) }

  override fun hashCode(): Int {
    var textHashCode = text.hashCode()
    if (textHashCode == 0) {
      textHashCode = 1
    }

    return HashUtil.hashCode(textHashCode, HashUtil.calculateHashFor(highlighters))
  }

  override fun equals(other: Any?): Boolean = other is HighlightedTextUiWrapper && other.hashCode() == hashCode()
}