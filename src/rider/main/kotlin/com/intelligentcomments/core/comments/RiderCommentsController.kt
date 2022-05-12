package com.intelligentcomments.core.comments

import com.intelligentcomments.core.comments.listeners.CommentsEditorsListenersManager
import com.intelligentcomments.core.comments.states.CommentState
import com.intelligentcomments.core.comments.states.RiderCommentsStateManager
import com.intelligentcomments.core.comments.storages.DocumentCommentsWithFoldingsStorage
import com.intelligentcomments.core.domain.core.CommentBase
import com.intelligentcomments.core.domain.core.CommentIdentifier
import com.intelligentcomments.core.settings.CommentsDisplayKind
import com.intelligentcomments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentcomments.ui.comments.model.CollapsedCommentUiModel
import com.intelligentcomments.ui.comments.renderers.CollapsedCommentRenderer
import com.intelligentcomments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.CustomFoldRegionRenderer
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
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
  private var isRunningBatchFoldingOperation = false
  private val commentsStorage: DocumentCommentsWithFoldingsStorage = DocumentCommentsWithFoldingsStorage()
  private val logger = getLogger<RiderDocumentHost>()
  private val commentsStateManager = project.getComponent(RiderCommentsStateManager::class.java)
  private val textControlHost = FrontendTextControlHost.getInstance(project)
  private val listenersManager = project.service<CommentsEditorsListenersManager>()
  private val settings = RiderIntelligentCommentsSettingsProvider.getInstance()


  fun findNearestLeftCommentToCurrentOffset(editor: Editor): CommentBase? {
    return commentsStorage.findNearestLeftCommentTo(editor, editor.caretModel.offset)
  }

  fun findNearestCommentToOffset(editor: Editor, offset: Int): CommentBase? {
    return commentsStorage.findNearestCommentTo(editor, offset)
  }

  fun getFolding(commentIdentifier: CommentIdentifier, editor: Editor): CustomFoldRegion? {
    return commentsStorage.getFolding(commentIdentifier, editor) as? CustomFoldRegion
  }

  fun getAllFoldingsFor(editor: Editor) = commentsStorage.getAllFoldingsFor(editor)

  fun getAllCommentsFor(editor: Editor) = commentsStorage.getAllComments(editor)

  fun addComments(editor: Editor, comments: Collection<CommentBase>) {
    application.assertIsDispatchThread()

    editor.runBatchFoldingOperation {
      for (comment in comments) {
        commentsStorage.addNewComment(comment, editor)
        val state = commentsStateManager.restoreOrCreateCommentState(editor, comment.identifier)
        updateCommentToMatchState(comment.identifier, editor.document, state)
      }
    }
  }

  private fun assertThatInBatchFoldingUpdate(editor: Editor) {
    application.assertIsDispatchThread()
    val model = editor.foldingModel as FoldingModelImpl
    if (model.isInBatchFoldingOperation) {
      logger.logAssertion("Calling addComment without BatchFoldingOperation")
    }
  }

  private fun Editor.runBatchFoldingOperation(action: () -> Unit) {
    application.assertIsDispatchThread()
    if (isRunningBatchFoldingOperation) {
      logger.logAssertion("Already running batch operation, may be bad for performance to run one more")
      action()
    }

    isRunningBatchFoldingOperation = true

    try {
      foldingModel.runBatchFoldingOperation {
        action()
      }
    } finally {
      isRunningBatchFoldingOperation = false
    }
  }

  fun toggleModeChange(
    commentIdentifier: CommentIdentifier,
    editor: Editor,
    transform: (CommentsDisplayKind) -> CommentsDisplayKind
  ) {
    executeWithCurrentState(commentIdentifier, editor) { commentState ->
      editor.runBatchFoldingOperation {
        changeStateAndUpdateComment(editor, commentIdentifier, transform(commentState.displayKind))
      }
    }
  }

  fun changeStatesForAllCommentsInEditor(
    editor: Editor,
    transform: (CommentsDisplayKind) -> CommentsDisplayKind
  ) {
    editor.runBatchFoldingOperation {
      val comments = commentsStorage.getAllComments(editor)
      for (comment in comments) {
        val actualState = commentsStateManager.changeDisplayKind(editor, comment.identifier, transform) ?: continue
        updateCommentToMatchState(comment.identifier, editor.document, actualState)
      }
    }
  }

  private fun executeWithCurrentState(
    commentIdentifier: CommentIdentifier,
    editor: Editor,
    action: (CommentState) -> Unit
  ) {
    application.assertIsDispatchThread()
    val comment = getComment(commentIdentifier, editor.document) ?: return
    val commentState = commentsStateManager.getExistingCommentState(editor, comment.identifier)
    if (commentState == null) {
      logger.logAssertion("Trying to change render mode for a not registered comment ${comment.identifier}")
      return
    }

    action(commentState)
  }

  fun collapseOrExpandAllFoldingsInCodeMode(editor: Editor) {
    val displayKind = RiderIntelligentCommentsSettingsProvider.getInstance().commentsDisplayKind.value
    if (displayKind != CommentsDisplayKind.Code) return

    editor.runBatchFoldingOperation {
      for (folding in commentsStorage.getAllFoldingsFor(editor)) {
        folding.isExpanded = !folding.isExpanded
      }
    }
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
      val id = comment.identifier
      executeWithCurrentState(id, editor) { state ->
        val cachedKind = state.displayKind
        editor.runBatchFoldingOperation {
          changeStateAndUpdateComment(editor, id, CommentsDisplayKind.Render)
        }

        lifetime.onTermination {
          editor.runBatchFoldingOperation {
            changeStateAndUpdateComment(editor, id, cachedKind)
          }
        }
      }
    }
  }

  private fun updateCommentToMatchState(
    commentIdentifier: CommentIdentifier,
    document: Document,
    state: CommentState
  ) {
    val documentId = document.getFirstDocumentId(project) ?: return
    for (editor in textControlHost.getAllEditors(documentId)) {
      doUpdateCommentToMathState(commentIdentifier, editor, state)
    }
  }

  private fun doUpdateCommentToMathState(commentIdentifier: CommentIdentifier, editor: Editor, state: CommentState) {
    val comment = getComment(commentIdentifier, editor.document)
    if (comment == null) {
      logger.logAssertion("comment == null")
      return
    }

    if (!state.isInRenderMode || !comment.isValid()) {
      toggleEditMode(commentIdentifier, editor)
    } else {
      toggleRenderMode(commentIdentifier, editor, state)
    }
  }

  private fun toggleEditMode(commentIdentifier: CommentIdentifier, editor: Editor) {
    application.assertIsDispatchThread()

    val correspondingComment = getComment(commentIdentifier, editor.document)
    if (correspondingComment != null) {
      val foldingModel = editor.foldingModel as FoldingModelImpl
      val folding = commentsStorage.getFolding(commentIdentifier, editor)
      var isFoldingExpanded = true
      if (folding != null) {
        if (!(folding is CustomFoldRegion && folding.renderer is RendererWithRectangleModel)) {
          isFoldingExpanded = folding.isExpanded
        }

        foldingModel.removeFoldRegion(folding)
        commentsStorage.removeFolding(commentIdentifier, editor)
      }

      val rangeMarker = commentIdentifier.rangeMarker
      val start = rangeMarker.startOffset
      val end = rangeMarker.endOffset
      val foldRegions = foldingModel.getRegionsOverlappingWith(start, end)
      for (region in foldRegions) {
        if (region.startOffset >= start && region.endOffset <= end) {
          foldingModel.removeFoldRegion(region)
        }
      }

      val region = foldingModel.createFoldRegion(rangeMarker.startOffset, rangeMarker.endOffset, "...", null, false)
      region?.isExpanded = isFoldingExpanded
      region?.markAsDocComment()
      if (region != null) {
        commentsStorage.addFoldingToComment(correspondingComment, region, editor)
      }
    }
  }

  private fun getComment(commentIdentifier: CommentIdentifier, document: Document): CommentBase? {
    return commentsStorage.getComment(commentIdentifier, document)
  }

  fun reRenderAllComments(editor: Editor) {
    commentsStorage.recreateAllCommentsFor(editor)
    editor.runBatchFoldingOperation {
      for (comment in commentsStorage.getAllComments(editor)) {
        val state = commentsStateManager.getExistingCommentState(editor, comment.identifier) ?: continue
        doUpdateCommentToMathState(comment.identifier, editor, state)
      }
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
    val rangeMarker = comment.identifier.rangeMarker
    state.lastRelativeCaretOffsetWithinComment = if (rangeMarker.range.contains(caretOffset)) {
      caretOffset - rangeMarker.startOffset
    } else {
      0
    }
  }

  private fun removeFoldRegion(editor: Editor, foldStartOffset: Int, foldEndOffset: Int) {
    assertThatInBatchFoldingUpdate(editor)
    val foldingModel = editor.foldingModel
    val oldFolding = foldingModel.getFoldRegion(foldStartOffset, foldEndOffset)
    if (oldFolding != null) {
      if (oldFolding is CustomFoldRegion && oldFolding.renderer is RendererWithRectangleModel) {
        val oldFoldingRenderer = oldFolding.renderer
        if (oldFoldingRenderer is RendererWithRectangleModel) {
          val oldComment = oldFoldingRenderer.baseModel.comment
          commentsStorage.removeFolding(oldComment.identifier, editor)
        }
      }

      foldingModel.removeFoldRegion(oldFolding)
      foldingModel as FoldingModelImpl
      foldingModel.updateCachedOffsets()
    }
  }

  data class FoldingInfo(val foldStartLine: Int, val foldEndLine: Int, val foldStartOffset: Int, val foldEndOffset: Int)

  private fun getFoldingInfo(comment: CommentBase, editor: Editor): FoldingInfo? {
    val rangeMarker = comment.identifier.rangeMarker
    if (!rangeMarker.isValid) return null
    val startOffset = rangeMarker.startOffset
    val endOffset = rangeMarker.endOffset
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
    assertThatInBatchFoldingUpdate(editor)
    val (foldStartLine, foldEndLine, foldStartOffset, foldEndOffset) = getFoldingInfo(comment, editor) ?: return
    removeFoldRegion(editor, foldStartOffset, foldEndOffset)

    val foldingModel = editor.foldingModel
    val renderer = getCommentFoldingRenderer(comment, editor, state)
    val folding = foldingModel.addCustomLinesFolding(foldStartLine, foldEndLine, renderer)

    if (folding == null) {
      logger.error("Failed to create folding region for ${comment.id}")
    } else {
      commentsStorage.addFoldingToComment(comment, folding, editor)
      listenersManager.attachListenersIfNeeded(folding)
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

    return comment.uiModel.renderer
  }
}