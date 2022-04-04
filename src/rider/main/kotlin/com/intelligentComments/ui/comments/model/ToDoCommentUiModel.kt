package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.ToDoComment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intelligentComments.ui.comments.renderers.ToDoCommentRenderer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

class ToDoCommentUiModel(
  comment: ToDoComment,
  project: Project,
  editor: Editor
) : CommentUiModelBase(comment, project, editor) {
  override val renderer: RendererWithRectangleModel = ToDoCommentRenderer(this)
  override val contentSection: SectionUiModel


  init {
    val segments = comment.toDoContent.segments.map { ContentSegmentUiModel.getFrom(project, this, it) }
    contentSection = SectionUiModel(project, this, segments)
  }


  override fun calculateStateHash(): Int {
    return contentSection.calculateStateHash()
  }
}