package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.comments.docs.CommentsHoverDocManager
import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.core.settings.CommentsDisplayKind
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlighterUiModel
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.CollapsedCommentRenderer
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.util.application
import com.jetbrains.rd.platform.diagnostics.logAssertion
import com.jetbrains.rd.platform.util.getLogger

class CollapsedCommentUiModel(
  comment: CommentBase,
  project: Project,
  editor: Editor
) : CommentUiModelBase(comment, project, editor) {
  companion object {
    private const val placeholder = "Collapsed comment..."
    private val logger = getLogger<CollapsedCommentUiModel>()
  }

  private val hoverDocsManager = project.service<CommentsHoverDocManager>()
  private val highlighterUiModel: HighlighterUiModel?

  override val contentSection: SectionUiModel


  init {
    val highlighter = CommonsHighlightersFactory.tryCreateCommentHighlighter(null, placeholder.length)
    val textSegmentUiModel = TextContentSegmentUiModel(project, this, object : UniqueEntityImpl(), TextContentSegment {
      override val highlightedText: HighlightedText = HighlightedTextImpl(placeholder, this, highlighter)
      override val parent: Parentable = comment
    })

    highlighterUiModel = textSegmentUiModel.highlightedTextWrapper.highlighters.firstOrNull()
    contentSection = SectionUiModel(project, this, listOf(textSegmentUiModel))
  }


  override val renderer: RendererWithRectangleModel
    get() = CollapsedCommentRenderer(this)

  override fun calculateStateHash(): Int {
    return contentSection.calculateStateHash()
  }

  override fun handleLongMousePresence(e: EditorMouseEvent): Boolean {
    hoverDocsManager.showHoverDoc(comment, e)
    return false
  }

  override fun handleMouseOutInternal(e: EditorMouseEvent): Boolean {
    application.invokeLater {
      hoverDocsManager.tryHideHoverDoc()
    }

    return false
  }

  override fun handleClick(e: EditorMouseEvent): Boolean {
    val currentState = commentsStateManager.getExistingCommentState(editor, comment.commentIdentifier)
    if (currentState == null) {
      logger.logAssertion("Failed to get comment's state for ${comment.commentIdentifier}")
      return false
    }

    if (currentState.displayKind == CommentsDisplayKind.Render) return false

    controller.toggleModeChange(comment.commentIdentifier, e.editor as EditorImpl) {
      if (it == CommentsDisplayKind.Hide) {
        CommentsDisplayKind.Code
      } else {
        RiderIntelligentCommentsSettingsProvider.getInstance().commentsDisplayKind.value
      }
    }

    return true
  }
}