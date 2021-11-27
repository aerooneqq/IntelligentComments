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
  private val highlightedTextWrapper = HighlightedTextUiWrapper(project, textSegment.highlightedText)
  val text = highlightedTextWrapper.text
  val highlighters = highlightedTextWrapper.highlighters

  override fun hashCode(): Int = highlightedTextWrapper.hashCode() % HashUtil.mod
  override fun equals(other: Any?): Boolean = other is TextContentSegmentUiModel && other.hashCode() == hashCode()
}