package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.ContentSegment
import com.intelligentComments.core.domain.core.ContentSegments
import com.intelligentComments.ui.comments.model.ModelWithContent
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ContentSegmentsUiModel : UiInteractionModelBase, ModelWithContent {
  override val content: Collection<ContentSegmentUiModel>


  constructor(
    project: Project,
    parent: UiInteractionModelBase?,
    content: ContentSegments
  ) : super(project, parent) {
    this.content = content.segments.map { ContentSegmentUiModel.getFrom(project, this, it) }
  }

  constructor(
    project: Project,
    parent: UiInteractionModelBase?,
    content: Collection<ContentSegment>
  ) : super(project, parent) {
    this.content = content.map { ContentSegmentUiModel.getFrom(project, this, it) }
  }


  override fun calculateStateHash(): Int {
    return HashUtil.calculateHashFor(content) { it.calculateStateHash() }
  }
}