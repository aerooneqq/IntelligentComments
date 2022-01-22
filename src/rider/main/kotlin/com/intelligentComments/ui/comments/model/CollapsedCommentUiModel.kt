package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.comments.RiderCommentsController
import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.CollapsedCommentRenderer
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project

class CollapsedCommentUiModel(
  comment: CommentBase,
  project: Project,
  editor: Editor
) : CommentUiModelBase(comment, project, editor) {
  companion object {
    private const val placeholder = "Collapsed comment (click to expand)"
  }

  override val contentSection: SectionUiModel<ContentSegmentUiModel>


  init {
    val textSegmentUiModel = TextContentSegmentUiModel(project, this, object : UniqueEntityImpl(), TextContentSegment {
      override val highlightedText: HighlightedText = HighlightedTextImpl(
        placeholder, this, CommonsHighlightersFactory.tryCreateCommentHighlighter(null, placeholder.length))
      override val parent: Parentable = comment
    })

    contentSection = SectionUiModel(project, this, listOf(textSegmentUiModel))
  }


  override val renderer: RendererWithRectangleModel
    get() = CollapsedCommentRenderer(this)

  override fun calculateStateHash(): Int {
    return contentSection.calculateStateHash()
  }

  override fun handleClick(e: EditorMouseEvent): Boolean {
    project.service<RiderCommentsController>().toggleModeChange(comment.commentIdentifier, e.editor as EditorImpl)
    return true
  }
}