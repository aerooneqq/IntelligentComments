package com.intelligentComments.ui.comments.model.content.text

import com.intelligentComments.core.domain.core.TextContentSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TextContentSegmentUiModel(
  project: Project,
  textSegment: TextContentSegment
) : ContentSegmentUiModel(project, textSegment) {
  val highlightedTextWrapper = HighlightedTextUiWrapper(project, textSegment.highlightedText)

  override fun hashCode(): Int = HashUtil.hashCode(highlightedTextWrapper.hashCode())
  override fun equals(other: Any?): Boolean = other is TextContentSegmentUiModel && other.hashCode() == hashCode()
}