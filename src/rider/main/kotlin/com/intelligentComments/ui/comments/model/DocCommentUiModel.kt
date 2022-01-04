package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.DocComment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.DocCommentRenderer
import com.intellij.openapi.editor.CustomFoldRegionRenderer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.project.Project
import com.intellij.psi.impl.source.tree.injected.changesHandler.range

class DocCommentUiModel(
  val docComment: DocComment,
  project: Project,
  val editor: Editor
) : UiInteractionModelBase(project, null), RootUiModel {
  override val renderer = DocCommentRenderer(this)

  val contentSection: SectionUiModel<ContentSegmentUiModel>
  val underlyingTextRange = docComment.rangeMarker.range


  init {
    val segments = docComment.content.segments.map { ContentSegmentUiModel.getFrom(project, this, it) }
    contentSection = SectionUiModel(project, this, segments)
  }

  override fun calculateStateHash(): Int {
    return contentSection.calculateStateHash()
  }
}