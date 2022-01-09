package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.CommentWithOneTextSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.CommentWithOneTextSegmentRenderer
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

class CommentWithOneTextSegmentUiModel(
  commentWithOneTextSegment: CommentWithOneTextSegment,
  project: Project,
  editor: Editor
) : CommentUiModelBase(commentWithOneTextSegment, project, editor) {
  override val renderer: RendererWithRectangleModel = CommentWithOneTextSegmentRenderer(this)
  override val content = listOf(ContentSegmentUiModel.getFrom(project, this, commentWithOneTextSegment.text))
  val contentSection: SectionUiModel<ContentSegmentUiModel> = SectionUiModel(project, parent, content)

  override fun calculateStateHash(): Int {
    return HashUtil.calculateHashFor(content) { it.calculateStateHash() }
  }
}