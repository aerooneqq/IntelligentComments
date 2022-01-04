package com.intelligentComments.ui.comments.model.content.text

import com.intelligentComments.core.domain.core.Parentable
import com.intelligentComments.core.domain.core.TextContentSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TextContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  textSegment: TextContentSegment
) : ContentSegmentUiModel(project, parent, textSegment) {
  val highlightedTextWrapper = HighlightedTextUiWrapper(project, this, textSegment.highlightedText)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(highlightedTextWrapper.calculateStateHash())
  }
}