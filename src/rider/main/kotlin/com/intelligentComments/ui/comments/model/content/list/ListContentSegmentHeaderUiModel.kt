package com.intelligentComments.ui.comments.model.content.list

import com.intelligentComments.core.domain.core.HighlightedText
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project

class ListContentSegmentHeaderUiModel(
  project: Project,
  highlightedText: HighlightedText,
  private val parent: ListContentSegmentUiModel
) : UiInteractionModelBase(project) {
  val textWrapper = HighlightedTextUiWrapper(project, highlightedText)

  override fun handleClick(e: EditorMouseEvent): Boolean {
    parent.isExpanded = !parent.isExpanded
    return true
  }

  override fun hashCode(): Int = HashUtil.hashCode(textWrapper.hashCode())
  override fun equals(other: Any?): Boolean = other is ListContentSegmentHeaderUiModel && other.hashCode() == hashCode()
}