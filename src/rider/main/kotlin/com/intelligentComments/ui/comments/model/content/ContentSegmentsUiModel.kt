package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.ContentSegments
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ContentSegmentsUiModel(project: Project, content: ContentSegments) : UiInteractionModelBase(project) {
  val content: Collection<ContentSegmentUiModel> = content.segments.map { ContentSegmentUiModel.getFrom(project, it) }

  override fun hashCode(): Int = HashUtil.calculateHashFor(content)
  override fun equals(other: Any?): Boolean = other is ContentSegmentsUiModel && other.hashCode() == hashCode()
}