package com.intelligentcomments.ui.comments.model.content.paragraphs

import com.intelligentcomments.core.domain.core.ParagraphContentSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.renderers.segments.ParagraphRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ParagraphUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  paragraph: ParagraphContentSegment
) : ContentSegmentUiModel(project, parent) {
  val content = ContentSegmentsUiModel(project, this, paragraph.content)


  override fun dumpModel() = "${super.dumpModel()}: \n{\n${content.dumpModel()}\n}"
  override fun calculateStateHash() = HashUtil.hashCode(content.calculateStateHash())
  override fun createRenderer() = ParagraphRenderer(this)
}