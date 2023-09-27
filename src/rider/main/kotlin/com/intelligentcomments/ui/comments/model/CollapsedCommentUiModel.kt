package com.intelligentcomments.ui.comments.model

import com.intelligentcomments.core.comments.docs.CommentsHoverDocManager
import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.core.domain.impl.HighlightedTextImpl
import com.intelligentcomments.core.domain.impl.emptyCommentPlaceholder
import com.intelligentcomments.core.settings.CommentsDisplayKind
import com.intelligentcomments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentcomments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.sections.SectionUiModel
import com.intelligentcomments.ui.comments.renderers.CollapsedCommentRenderer
import com.intelligentcomments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.util.application
import com.jetbrains.rd.platform.util.getLogger


class CollapsedCommentUiModel(
  comment: CommentBase,
  project: Project,
  editor: Editor
) : CommentUiModelBase(comment, project, editor) {
  companion object {
    private const val placeholder = emptyCommentPlaceholder
    private val logger = getLogger<CollapsedCommentUiModel>()
  }

  private val hoverDocsManager = project.service<CommentsHoverDocManager>()

  override val contentSection: SectionUiModel


  init {
    val highlighter = CommonsHighlightersFactory.tryCreateCommentHighlighter(null, placeholder.length)
    val textSegmentUiModel = TextContentSegmentUiModel(project, this, object : UniqueEntityImpl(), TextContentSegment {
      override val highlightedText: HighlightedText = HighlightedTextImpl(placeholder, this, highlighter)
      override val parent: Parentable = comment
    })

    contentSection = SectionUiModel(project, this, listOf(textSegmentUiModel))
  }


  override fun dumpModel(): String = "${super.dumpModel()}::${contentSection.dumpModel()}"

  override val renderer: RendererWithRectangleModel = CollapsedCommentRenderer(this)

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
    val currentState = commentsStateManager.getExistingCommentState(editor, comment.identifier)
    if (currentState == null) {
      logger.error("Failed to get comment's state for ${comment.identifier}")
      return false
    }

    if (currentState.displayKind == CommentsDisplayKind.Render) return false

    controller.toggleModeChange(comment.identifier, e.editor as EditorImpl) {
      if (it == CommentsDisplayKind.Hide) {
        CommentsDisplayKind.Code
      } else {
        RiderIntelligentCommentsSettingsProvider.getInstance().commentsDisplayKind.value
      }
    }

    return true
  }
}