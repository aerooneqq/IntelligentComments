package com.intelligentComments.ui.comments.model.content.remarks

import com.intelligentComments.core.domain.core.RemarksSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intellij.openapi.project.Project

class RemarksUiModel(
  project: Project,
  remarksSegment: RemarksSegment
) : ContentSegmentUiModel(project, remarksSegment) {
  val content = ContentSegmentsUiModel(project, remarksSegment.content)
}