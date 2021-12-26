package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.ContentSegment
import com.intelligentComments.core.domain.core.ContentSegments
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ContentSegmentsUiModel : UiInteractionModelBase {
  val content: Collection<ContentSegmentUiModel>


  constructor(project: Project, content: ContentSegments) : super(project) {
    this.content = content.segments.map { ContentSegmentUiModel.getFrom(project, it) }
  }

  constructor(project: Project, content: Collection<ContentSegment>) : super(project) {
    this.content = content.map { ContentSegmentUiModel.getFrom(project, it) }
  }


  override fun hashCode(): Int = HashUtil.calculateHashFor(content)
  override fun equals(other: Any?): Boolean = other is ContentSegmentsUiModel && other.hashCode() == hashCode()
}