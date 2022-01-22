package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.CommentWithOneTextSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.CommentWithOneTextSegmentRenderer
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

class CommentWithOneTextSegmentUiModel(
  commentWithOneTextSegment: CommentWithOneTextSegment,
  project: Project,
  editor: Editor
) : CommentUiModelBase(commentWithOneTextSegment, project, editor) {
  override val renderer: RendererWithRectangleModel
    get() = CommentWithOneTextSegmentRenderer(this)

  override val contentSection: SectionUiModel<ContentSegmentUiModel>


  init {
    val content = listOf(ContentSegmentUiModel.getFrom(project, this, commentWithOneTextSegment.text))
    contentSection = SectionUiModel(project, parent, content)
  }


  override fun calculateStateHash(): Int {
    return contentSection.calculateStateHash()
  }
}