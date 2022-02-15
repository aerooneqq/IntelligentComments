package com.intelligentComments.core.comments

import com.intelligentComments.core.comments.listeners.CommentsEditorsListenersManager
import com.intelligentComments.core.comments.states.CommentState
import com.intelligentComments.core.comments.states.RiderCommentsStateManager
import com.intelligentComments.core.comments.storages.DocumentCommentsWithFoldingsStorage
import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.settings.CommentsDisplayKind
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentComments.ui.comments.model.*
import com.intelligentComments.ui.comments.renderers.CollapsedCommentRenderer
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.impl.FoldingModelImpl
import com.intellij.openapi.project.Project
import com.intellij.psi.impl.source.tree.injected.changesHandler.range
import com.intellij.util.application
import com.jetbrains.rd.platform.diagnostics.logAssertion
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.lifetime.onTermination
import com.jetbrains.rdclient.daemon.highlighters.foldings.markAsDocComment
import com.jetbrains.rdclient.document.getFirstDocumentId
import com.jetbrains.rdclient.editors.FrontendTextControlHost
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent
import com.jetbrains.rider.document.RiderDocumentHost


class RiderCommentsController(project: Project) : LifetimedProjectComponent(project) {
  private val commentsStorage: DocumentCommentsWithFoldingsStorage = DocumentCommentsWithFoldingsStorage()
  private val logger = getLogger<RiderDocumentHost>()
  private val commentsStateManager = project.getComponent(RiderCommentsStateManager::class.java)
  private val textControlHost = FrontendTextControlHost.getInstance(project)
  private val listenersManager = project.service<CommentsEditorsListenersManager>()
  private val settings = RiderIntelligentCommentsSettingsProvider.getInstance()


  fun findNearestCommentToCurrentOffset(editor: Editor): CommentBase? {
    return commentsStorage.findNearestCommentTo(editor, editor.caretModel.offset)
  }

  fun getFolding(commentIdentifier: CommentIdentifier, editor: Editor): CustomFoldRegion? {
    return commentsStorage.getFolding(commentIdentifier, editor)
  }

  fun getAllFoldingsFor(editor: Editor) = commentsStorage.getAllFoldingsFor(editor)

  fun addComment(editor: Editor, comment: CommentBase) {
    application.assertIsDispatchThread()

    if (!comment.isValid()) {
      logger.warn("Comment ${comment.commentIdentifier} is invalid")
      return
    }

    commentsStorage.addNewComment(comment, editor)
    val state = commentsStateManager.restoreOrCreateCommentState(editor, comment.commentIdentifier)
    updateCommentToMatchState(comment.commentIdentifier, editor.document, state)
  }

  fun toggleModeChange(
    commentIdentifier: CommentIdentifier,
    editor: Editor,
    transform: (CommentsDisplayKind) -> CommentsDisplayKind
  ) {
    executeWithCurrentState(commentIdentifier, editor) { commentState ->
      changeStateAndUpdateComment(editor, commentIdentifier, transform(commentState.displayKind))
    }
  }

  fun changeStatesForAllCommentsInEditor(
    editor: Editor,
    transform: (CommentsDisplayKind) -> CommentsDisplayKind
  ) {
    val comments = commentsStorage.getAllComments(editor)
    for (comment in comments) {
      val actualState = commentsStateManager.changeDisplayKind(editor, comment.commentIdentifier, transform) ?: continue
      updateCommentToMatchState(comment.commentIdentifier, editor.document, actualState)
    }
  }

  private fun executeWithCurrentState(
    commentIdentifier: CommentIdentifier,
    editor: Editor,
    action: (CommentState) -> Unit
  ) {
    application.assertIsDispatchThread()
    val comment = getComment(commentIdentifier, editor.document) ?: return
    val commentState = commentsStateManager.getExistingCommentState(editor, comment.commentIdentifier)
    if (commentState == null) {
      logger.logAssertion("Trying to change render mode for a not registered comment ${comment.commentIdentifier}")
      return
    }

    action(commentState)
  }

  private fun changeStateAndUpdateComment(
    editor: Editor,
    commentIdentifier: CommentIdentifier,
    newDisplayKind: CommentsDisplayKind
  ) {
    val changedState = commentsStateManager.changeDisplayKind(editor, commentIdentifier, newDisplayKind)
    if (changedState != null) {
      updateCommentToMatchState(commentIdentifier, editor.document, changedState)
    }
  }

  fun displayInRenderMode(comment: CommentBase, editor: Editor, lifetime: Lifetime) {
    lifetime.executeIfAlive {
      val id = comment.commentIdentifier
      executeWithCurrentState(id, editor) { state ->
        val cachedKind = state.displayKind
        changeStateAndUpdateComment(editor, id, CommentsDisplayKind.Render)
        lifetime.onTermination {
          changeStateAndUpdateComment(editor, id, cachedKind)
        }
      }
    }
  }

  private fun updateCommentToMatchState(
    commentIdentifier: CommentIdentifier,
    document: Document,
    state: CommentState
  ) {
    application.invokeLater {
      val documentId = document.getFirstDocumentId(project) ?: return@invokeLater
      for (editor in textControlHost.getAllEditors(documentId)) {
        editor as? EditorImpl ?: continue
        doUpdateCommentToMathState(commentIdentifier, editor, state)
      }
    }
  }

  private fun doUpdateCommentToMathState(commentIdentifier: CommentIdentifier, editor: Editor, state: CommentState) {
    if (!state.isInRenderMode) {
      toggleEditMode(commentIdentifier, editor, state)
    } else {
      toggleRenderMode(commentIdentifier, editor, state)
    }
  }

  private fun toggleEditMode(commentIdentifier: CommentIdentifier, editor: Editor, state: CommentState) {
    application.assertIsDispatchThread()

    val correspondingComment = getComment(commentIdentifier, editor.document)
    if (correspondingComment != null) {
      val foldingModel = editor.foldingModel as FoldingModelImpl
      val folding = commentsStorage.getFolding(commentIdentifier, editor)
      if (folding != null) {
        val startOffset = commentIdentifier.rangeMarker.startOffset
        editor.caretModel.moveToOffset(state.lastRelativeCaretOffsetWithinComment + startOffset)
        foldingModel.runBatchFoldingOperation {
          foldingModel.removeFoldRegion(folding)
          commentsStorage.removeFolding(commentIdentifier, editor)
        }
      }

      foldingModel.runBatchFoldingOperation {
        val startOffset = commentIdentifier.rangeMarker.startOffset
        val endOffset = commentIdentifier.rangeMarker.endOffset
        val region = foldingModel.createFoldRegion(startOffset, endOffset, "...", null, false)
        region?.markAsDocComment()
      }
    }
  }

  private fun getComment(commentIdentifier: CommentIdentifier, document: Document): CommentBase? {
    return commentsStorage.getComment(commentIdentifier, document)
  }

  fun reRenderAllComments(editor: Editor) {
    commentsStorage.recreateAllCommentsFor(editor)
    for (comment in commentsStorage.getAllComments(editor)) {
      val state = commentsStateManager.getExistingCommentState(editor, comment.commentIdentifier) ?: continue
      doUpdateCommentToMathState(comment.commentIdentifier, editor, state)
    }
  }

  private fun toggleRenderMode(commentId: CommentIdentifier, editor: Editor, state: CommentState) {
    application.assertIsDispatchThread()

    val correspondingComment = getComment(commentId, editor.document)

    if (correspondingComment != null && settings.commentsDisplayKind.value != CommentsDisplayKind.Code) {
      cacheCaretOffset(correspondingComment, state, editor)
      renderComment(correspondingComment, editor, state)
    }
  }

  private fun cacheCaretOffset(comment: CommentBase, state: CommentState, editor: Editor) {
    val caretOffset = editor.caretModel.offset
    state.lastRelativeCaretOffsetWithinComment = if (comment.rangeMarker.range.contains(caretOffset)) {
      caretOffset - comment.rangeMarker.startOffset
    } else {
      0
    }
  }

  private fun removeFoldRegion(editor: Editor, foldStartOffset: Int, foldEndOffset: Int) {
    val foldingModel = editor.foldingModel
    foldingModel.runBatchFoldingOperation {
      val oldFolding = foldingModel.getFoldRegion(foldStartOffset, foldEndOffset)
      if (oldFolding != null) {
        if (oldFolding is CustomFoldRegion && oldFolding.renderer is RendererWithRectangleModel) {
          val oldFoldingRenderer = oldFolding.renderer
          if (oldFoldingRenderer is RendererWithRectangleModel) {
            val oldComment = oldFoldingRenderer.baseModel.comment
            commentsStorage.removeFolding(oldComment.commentIdentifier, editor)
          }
        }

        foldingModel.removeFoldRegion(oldFolding)
      }
    }
  }

  data class FoldingInfo(val foldStartLine: Int, val foldEndLine: Int, val foldStartOffset: Int, val foldEndOffset: Int)

  private fun getFoldingInfo(comment: CommentBase, editor: Editor): FoldingInfo? {
    if (!comment.rangeMarker.isValid) return null
    val startOffset = comment.rangeMarker.startOffset
    val endOffset = comment.rangeMarker.endOffset
    val document = editor.document

    val foldStartLine = document.getLineNumber(startOffset)
    val foldEndLine = document.getLineNumber(endOffset)
    val foldStartOffset = document.getLineStartOffset(foldStartLine)
    val foldEndOffset = document.getLineEndOffset(foldEndLine)

    return FoldingInfo(foldStartLine, foldEndLine, foldStartOffset, foldEndOffset)
  }

  private fun renderComment(
    comment: CommentBase,
    editor: Editor,
    state: CommentState
  ) {
    val (foldStartLine, foldEndLine, foldStartOffset, foldEndOffset) = getFoldingInfo(comment, editor) ?: return
    removeFoldRegion(editor, foldStartOffset, foldEndOffset)

    val foldingModel = editor.foldingModel
    foldingModel.runBatchFoldingOperation {
      val renderer = getCommentFoldingRenderer(comment, editor, state)
      val folding = foldingModel.addCustomLinesFolding(foldStartLine, foldEndLine, renderer)

      if (folding == null) {
        logger.error("Failed to create folding region for ${comment.id}")
      } else {
        commentsStorage.addFoldingToComment(comment, folding, editor)
        listenersManager.attachListenersIfNeeded(folding)
      }
    }
  }

  private fun getCommentFoldingRenderer(
    comment: CommentBase,
    editor: Editor,
    state: CommentState
  ): CustomFoldRegionRenderer {
    if (state.displayKind == CommentsDisplayKind.Hide) {
      val collapsedUiModel = CollapsedCommentUiModel(comment, project, editor)
      return CollapsedCommentRenderer(collapsedUiModel)
    }

    val model = comment.createCommentUiModel(project, editor as EditorImpl)
    return model.renderer
  }
}

fun CommentBase.createCommentUiModel(project: Project, editor: Editor): CommentUiModelBase {
  return when (val comment = this) {
    is DocComment -> DocCommentUiModel(comment, project, editor)
    is IntelligentComment -> IntelligentCommentUiModel(project, comment)
    is CommentWithOneTextSegment -> CommentWithOneTextSegmentUiModel(comment, project, editor)
    else -> throw IllegalArgumentException(comment.javaClass.name)
  }
}