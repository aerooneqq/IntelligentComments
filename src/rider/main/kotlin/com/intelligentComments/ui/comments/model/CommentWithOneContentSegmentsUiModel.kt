package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.CommentWithOneContentSegments
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intelligentComments.ui.comments.renderers.CommentWithOneContentSegmentsRenderer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

class CommentWithOneContentSegmentsUiModel(
  comment: CommentWithOneContentSegments,
  project: Project,
  editor: Editor
) : CommentUiModelBase(comment, project, editor) {
  override val renderer: RendererWithRectangleModel = CommentWithOneContentSegmentsRenderer(this)
  override val contentSection: SectionUiModel


  init {
    val segments = comment.content.segments.map { ContentSegmentUiModel.getFrom(project, this, it) }
    contentSection = SectionUiModel(project, this, segments)
  }


  override fun calculateStateHash(): Int {
    return contentSection.calculateStateHash()
  }
}