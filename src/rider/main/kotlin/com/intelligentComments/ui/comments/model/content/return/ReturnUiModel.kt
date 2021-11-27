package com.intelligentComments.ui.comments.model.content.`return`

import com.intelligentComments.core.domain.core.HighlightedTextImpl
import com.intelligentComments.core.domain.core.ReturnSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intellij.openapi.project.Project

class ReturnUiModel(project: Project, ret: ReturnSegment) : ContentSegmentUiModel(project, ret) {
  companion object {
    const val returnSectionName = "Returns:"
  }

  val content = ContentSegmentsUiModel(project, ret.content)
  val headerText = HighlightedTextUiWrapper(project, HighlightedTextImpl(returnSectionName, emptyList()))

  override fun hashCode(): Int = content.hashCode()
  override fun equals(other: Any?): Boolean = other is ReturnUiModel && other.hashCode() == hashCode()
}