package com.intelligentComments.ui.comments.model.content.exceptions

import com.intelligentComments.core.domain.core.ExceptionSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.content.getSecondLevelHeader
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ExceptionUiModel(
  project: Project,
  exceptionSegment: ExceptionSegment
) : ContentSegmentUiModel(project, exceptionSegment) {
  val name = HighlightedTextUiWrapper(project, getSecondLevelHeader(project, exceptionSegment.name))
  val content = ContentSegmentsUiModel(project, exceptionSegment.content)

  override fun hashCode(): Int = HashUtil.hashCode(name.hashCode(), content.hashCode())

  override fun equals(other: Any?): Boolean = other is ExceptionUiModel && other.hashCode() == hashCode()
}