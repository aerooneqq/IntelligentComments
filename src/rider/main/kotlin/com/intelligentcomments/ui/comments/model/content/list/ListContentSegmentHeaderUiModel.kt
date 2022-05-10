package com.intelligentcomments.ui.comments.model.content.list

import com.intelligentcomments.core.domain.core.HighlightedText
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project

class ListContentSegmentHeaderUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  highlightedText: HighlightedText
) : UiInteractionModelBase(project, parent) {
  val textWrapper = HighlightedTextUiWrapper(project, this, highlightedText)

  override fun handleClick(e: EditorMouseEvent): Boolean {
    parent as ListContentSegmentUiModel
    parent.isExpanded = !parent.isExpanded
    return true
  }


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(textWrapper.calculateStateHash())
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}