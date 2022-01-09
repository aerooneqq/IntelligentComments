package com.intelligentComments.core.comments

import com.intelligentComments.core.comments.listeners.CommentsEditorsListenersManager
import com.intelligentComments.core.comments.states.CommentState
import com.intelligentComments.core.comments.states.RiderCommentsStateManager
import com.intelligentComments.core.comments.storages.DocumentCommentsWithFoldingsStorage
import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.comments.model.CommentWithOneTextSegmentUiModel
import com.intelligentComments.ui.comments.model.DocCommentUiModel
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.CustomFoldRegionRenderer
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.psi.impl.source.tree.injected.changesHandler.range
import com.intellij.util.application
import com.jetbrains.rd.platform.diagnostics.logAssertion
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rdclient.document.getFirstDocumentId
import com.jetbrains.rdclient.editors.FrontendTextControlHost
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent
import com.jetbrains.rider.document.RiderDocumentHost


class RiderCommentsController(project: Project) : LifetimedProjectComponent(project) {
  private val commentsStorage: DocumentCommentsWithFoldingsStorage = DocumentCommentsWithFoldingsStorage()
  private val logger = getLogger<RiderDocumentHost>()
  private val commentsStateManager = project.getService(RiderCommentsStateManager::class.java)
  private val textControlHost = FrontendTextControlHost.getInstance(project)
  private val listenersManager = project.service<CommentsEditorsListenersManager>()


  fun getFolding(commentIdentifier: CommentIdentifier, editor: EditorImpl): CustomFoldRegion? {
    return commentsStorage.getFolding(commentIdentifier, editor)
  }

  fun getAllFoldingsFor(editor: Editor) = commentsStorage.getAllFoldingsFor(editor)

  fun addComment(editor: EditorImpl, comment: CommentBase) {
    application.assertIsDispatchThread()

    if (!comment.isValid()) {
      logger.warn("Comment ${comment.commentIdentifier} is invalid")
      return
    }

    commentsStorage.addNewComment(comment, editor)
    val state = commentsStateManager.restoreOrCreateCommentState(editor, comment.commentIdentifier)
    updateRenderModeToMatchState(comment.commentIdentifier, editor.document, state)
  }

  fun toggleModeChange(commentIdentifier: CommentIdentifier, editor: EditorImpl) {
    application.assertIsDispatchThread()

    val comment = getComment(commentIdentifier, editor.document) ?: return
    val commentState = commentsStateManager.getExistingCommentState(editor, comment.commentIdentifier)
    if (commentState == null) {
      logger.logAssertion("Trying to change render mode for a not registered comment ${comment.commentIdentifier}")
      return
    }

    val changedState = commentsStateManager.changeRenderMode(editor, comment.commentIdentifier)
    if (changedState != null) {
      updateRenderModeToMatchState(commentIdentifier, editor.document, changedState)
    }
  }

  private fun updateRenderModeToMatchState(commentIdentifier: CommentIdentifier, document: Document, state: CommentState) {
    val documentId = document.getFirstDocumentId(project) ?: return
    for (editor in textControlHost.getAllEditors(documentId)) {
      editor as? EditorImpl ?: continue
      if (!state.isInRenderMode) {
        toggleEditMode(commentIdentifier, editor, state)
      } else {
        toggleRenderMode(commentIdentifier, editor, state)
      }
    }
  }

  private fun toggleEditMode(commentIdentifier: CommentIdentifier, editor: EditorImpl, state: CommentState) {
    application.assertIsDispatchThread()

    val correspondingComment = getComment(commentIdentifier, editor.document)
    if (correspondingComment != null) {
      val foldingModel = editor.foldingModel
      val folding = commentsStorage.getFolding(commentIdentifier, editor)
      if (folding != null) {
        val startOffset = commentIdentifier.rangeMarker.startOffset
        editor.caretModel.moveToOffset(state.lastRelativeCaretOffsetWithinComment + startOffset)
        foldingModel.runBatchFoldingOperation {
          foldingModel.removeFoldRegion(folding)
          commentsStorage.removeFolding(commentIdentifier, editor)
        }
      }
    }
  }

  private fun getComment(commentIdentifier: CommentIdentifier, document: Document): CommentBase? {
    return commentsStorage.getComment(commentIdentifier, document)
  }

  fun reRenderAllComments(editor: EditorImpl) {
    commentsStorage.recreateAllCommentsFor(editor)
    for (comment in commentsStorage.getAllComments(editor)) {
      val state = commentsStateManager.getExistingCommentState(editor, comment.commentIdentifier) ?: continue
      if (state.isInRenderMode) {
        toggleRenderMode(comment.commentIdentifier, editor, state)
      }
    }
  }

  private fun toggleRenderMode(commentId: CommentIdentifier, editor: EditorImpl, state: CommentState) {
    application.assertIsDispatchThread()

    val correspondingComment = getComment(commentId, editor.document)

    if (correspondingComment != null) {
      cacheCaretOffset(correspondingComment, state, editor)
      renderComment(correspondingComment, editor)
    }
  }

  private fun cacheCaretOffset(comment: CommentBase, state: CommentState, editor: EditorImpl) {
    val caretOffset = editor.caretModel.offset
    state.lastRelativeCaretOffsetWithinComment = if (comment.rangeMarker.range.contains(caretOffset)) {
      caretOffset - comment.rangeMarker.startOffset
    } else {
      0
    }
  }

  private fun renderComment(comment: CommentBase, editor: EditorImpl) {
    val foldingModel = editor.foldingModel
    val startOffset = comment.rangeMarker.startOffset
    val endOffset = comment.rangeMarker.endOffset
    val document = editor.document

    val foldStartLine = document.getLineNumber(startOffset)
    val foldEndLine = document.getLineNumber(endOffset)
    val foldStartOffset = document.getLineStartOffset(foldStartLine)
    val foldEndOffset = document.getLineEndOffset(foldEndLine)

    foldingModel.runBatchFoldingOperation {
      val oldFolding = foldingModel.getFoldRegion(foldStartOffset, foldEndOffset)
      if (oldFolding != null && oldFolding is CustomFoldRegion && oldFolding.renderer is RendererWithRectangleModel) {
        val oldFoldingRenderer = oldFolding.renderer
        if (oldFoldingRenderer is RendererWithRectangleModel) {
          val oldComment = oldFoldingRenderer.baseModel.comment
          commentsStorage.removeFolding(oldComment.commentIdentifier, editor)
          foldingModel.removeFoldRegion(oldFolding)
        }
      }
    }

    foldingModel.runBatchFoldingOperation {
      val renderer = getCommentFoldingRenderer(comment, editor)
      val folding = foldingModel.addCustomLinesFolding(foldStartLine, foldEndLine, renderer)

      if (folding == null) {
        logger.error("Failed to create folding region for ${comment.id}")
      } else {
        commentsStorage.addFoldingToComment(comment, folding, editor)
        listenersManager.attachListenersIfNeeded(folding)
      }
    }
  }

  private fun getCommentFoldingRenderer(comment: CommentBase, editor: Editor): CustomFoldRegionRenderer {
    return when (comment) {
      is DocComment -> DocCommentUiModel(comment, project, editor).renderer
      is IntelligentComment -> IntelligentCommentUiModel(project, comment).renderer
      is CommentWithOneTextSegment -> CommentWithOneTextSegmentUiModel(comment, project, editor).renderer
      else -> throw IllegalArgumentException(comment.javaClass.name)
    }
  }
}