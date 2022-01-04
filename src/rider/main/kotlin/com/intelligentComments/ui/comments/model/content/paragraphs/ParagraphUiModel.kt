package com.intelligentComments.ui.comments.model.content.paragraphs

import com.intelligentComments.core.domain.core.ParagraphContentSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ParagraphUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  paragraph: ParagraphContentSegment
) : ContentSegmentUiModel(project, parent, paragraph) {
  val content = ContentSegmentsUiModel(project, this, paragraph.content)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(content.calculateStateHash())
  }
}