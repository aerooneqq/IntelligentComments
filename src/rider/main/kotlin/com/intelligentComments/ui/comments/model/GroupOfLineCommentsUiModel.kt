package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.GroupOfLineComments
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.GroupOfCommentsRenderer
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

class GroupOfLineCommentsUiModel(
  comment: GroupOfLineComments,
  project: Project,
  editor: Editor
) : CommentUiModelBase(comment, project, editor) {
  override val renderer: RendererWithRectangleModel = GroupOfCommentsRenderer(this)
  override val content = listOf(ContentSegmentUiModel.getFrom(project, this, comment.text))
  val contentSection: SectionUiModel<ContentSegmentUiModel> = SectionUiModel(project, parent, content)

  override fun calculateStateHash(): Int {
    return HashUtil.calculateHashFor(content) { it.calculateStateHash() }
  }
}