package com.intelligentcomments.ui.comments.model

import com.intelligentcomments.core.domain.core.DocComment
import com.intelligentcomments.ui.comments.model.sections.SectionUiModel
import com.intelligentcomments.ui.comments.renderers.DocCommentRenderer
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
    val segments = docComment.content.content.segments.map { it.createUiModel(project, this) }
    contentSection = SectionUiModel(project, this, segments)
  }

  override fun dumpModel(): String {
    return "${super.dumpModel()}::${contentSection.dumpModel()}"
  }

  override fun calculateStateHash(): Int {
    return contentSection.calculateStateHash()
  }
}