package com.intelligentcomments.ui.comments.model

import com.intelligentcomments.core.domain.core.CommentWithOneTextSegment
import com.intelligentcomments.ui.comments.model.sections.SectionUiModel
import com.intelligentcomments.ui.comments.renderers.CommentWithOneTextSegmentRenderer
import com.intelligentcomments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

class CommentWithOneTextSegmentUiModel(
  commentWithOneTextSegment: CommentWithOneTextSegment,
  project: Project,
  editor: Editor
) : CommentUiModelBase(commentWithOneTextSegment, project, editor) {
  override val renderer: RendererWithRectangleModel = CommentWithOneTextSegmentRenderer(this)

  override val contentSection: SectionUiModel


  init {
    val content = listOf(commentWithOneTextSegment.text.createUiModel(project, this))
    contentSection = SectionUiModel(project, parent, content)
  }


  override fun calculateStateHash(): Int {
    return contentSection.calculateStateHash()
  }
}