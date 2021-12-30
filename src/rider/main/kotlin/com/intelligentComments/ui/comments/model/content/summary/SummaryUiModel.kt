package com.intelligentComments.ui.comments.model.content.summary

import com.intelligentComments.core.domain.core.SummaryContentSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class SummaryUiModel(
  project: Project,
  summary: SummaryContentSegment
) : ContentSegmentUiModel(project, summary) {
  val content = ContentSegmentsUiModel(project, summary.content)

  override fun hashCode() = HashUtil.hashCode(content.hashCode())
  override fun equals(other: Any?) = other is SummaryUiModel && other.hashCode() == hashCode()
}