package com.intelligentComments.ui.comments.model.content.paragraphs

import com.intelligentComments.core.domain.core.ParagraphContentSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intellij.openapi.project.Project

class ParagraphUiModel(
  project: Project,
  paragraph: ParagraphContentSegment
) : ContentSegmentUiModel(project, paragraph) {
  val content = ContentSegmentsUiModel(project, paragraph.content)
}