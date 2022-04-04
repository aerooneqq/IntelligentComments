package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.DocComment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.DocCommentRenderer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

class DocCommentUiModel(
  docComment: DocComment,
  project: Project,
  editor: Editor
) : CommentUiModelBase(docComment, project, editor) {
  override val renderer = DocCommentRenderer(this)
  override val contentSection: SectionUiModel


  init {
    val segments = docComment.content.content.segments.map { ContentSegmentUiModel.getFrom(project, this, it) }
    contentSection = SectionUiModel(project, this, segments)
  }


  override fun calculateStateHash(): Int {
    return contentSection.calculateStateHash()
  }
}