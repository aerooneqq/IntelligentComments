package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.DocComment
import com.intelligentComments.core.domain.core.IntelligentComment
import com.intelligentComments.ui.comments.model.DocCommentUiModel
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intelligentComments.ui.comments.renderers.DocCommentRenderer
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.CustomFoldRegionRenderer
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.jetbrains.rd.platform.diagnostics.logAssertion
import com.jetbrains.rd.platform.util.application
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.util.reactive.ViewableMap
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent
import com.jetbrains.rider.document.RiderDocumentHost
import java.util.*


class RiderCommentsController(project: Project) : LifetimedProjectComponent(project) {
  private val comments = ViewableMap<Document, ViewableMap<UUID, CommentBase>>()
  private val foldings = ViewableMap<UUID, CustomFoldRegion>()
  private val logger = getLogger<RiderDocumentHost>()
  private val commentsStateManager = project.getService(RiderCommentsStateManager::class.java)


  fun addComment(editor: EditorImpl, comment: CommentBase) {
    application.assertIsDispatchThread()

    val document = editor.document
    val documentComments = if (document !in comments) {
      val map = ViewableMap<UUID, CommentBase>()
      comments[document] = map
      map
    } else {
      comments[document] ?: return
    }

    val uuid = comment.id
    documentComments[uuid] = comment

    val state = commentsStateManager.getOrCreateCommentState(editor, comment.commentIdentifier)
    updateRenderModeToMatchState(comment.id, editor, state)
  }

  fun toggleModeChange(commentId: UUID, editor: EditorImpl) {
    application.assertIsDispatchThread()
    val comment = getComment(commentId, editor) ?: return
    val commentState = commentsStateManager.getExistingCommentState(editor, comment.commentIdentifier)
    if (commentState == null) {
      logger.logAssertion("Trying to change render mode for a not registered comment $commentId")
      return
    }

    val changedState = commentsStateManager.changeRenderMode(editor, comment.commentIdentifier)
    if (changedState != null) {
      updateRenderModeToMatchState(commentId, editor, changedState)
    }
  }

  private fun updateRenderModeToMatchState(commentId: UUID, editor: EditorImpl, state: CommentState) {
    if (!state.isInRenderMode) {
      toggleEditMode(commentId, editor)
    } else {
      toggleRenderMode(commentId, editor)
    }
  }

  private fun toggleEditMode(commentId: UUID, editor: EditorImpl) {
    application.assertIsDispatchThread()

    val correspondingComment = getComment(commentId, editor)
    if (correspondingComment != null) {
      val foldingModel = editor.foldingModel
      val folding = foldings[commentId]
      if (folding != null) {
        foldingModel.runBatchFoldingOperation {
          foldingModel.removeFoldRegion(folding)
          foldings.remove(commentId)
        }
      }
    }
  }

  private fun getComment(commentId: UUID, editor: EditorImpl): CommentBase? {
    val document = editor.document
    val documentComments = comments[document]
    val comment = documentComments?.get(commentId)

    if (comment == null){
      logger.error("Comment for giver ID $commentId does not exist")
    }

    return comment
  }

  private fun toggleRenderMode(commentId: UUID, editor: EditorImpl) {
    application.assertIsDispatchThread()

    val correspondingComment = getComment(commentId, editor)

    if (correspondingComment != null) {
      renderComment(correspondingComment, editor)
    }
  }

  private fun renderComment(comment: CommentBase, editor: EditorImpl) {
    val foldingModel = editor.foldingModel
    val startOffset = comment.highlighter.startOffset
    val endOffset = comment.highlighter.endOffset
    val document = editor.document

    val foldStartLine = document.getLineNumber(startOffset)
    val foldEndLine = document.getLineNumber(endOffset)
    val foldStartOffset = document.getLineStartOffset(foldStartLine)
    val foldEndOffset = document.getLineEndOffset(foldEndLine)

    foldingModel.runBatchFoldingOperation {
      val oldFolding = foldingModel.getFoldRegion(foldStartOffset, foldEndOffset)
      if (oldFolding != null && oldFolding is CustomFoldRegion && oldFolding.renderer is DocCommentRenderer) {
        val oldFoldingRenderer = oldFolding.renderer
        if (oldFoldingRenderer is DocCommentRenderer) {
          val oldComment = oldFoldingRenderer.model.docComment
          foldings.remove(oldComment.id)
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
        foldings[comment.id] = folding
      }
    }
  }

  private fun getCommentFoldingRenderer(comment: CommentBase, editor: Editor): CustomFoldRegionRenderer {
    return when (comment) {
      is DocComment -> DocCommentUiModel(comment, project, editor).getCustomFoldRegionRenderer(project)
      is IntelligentComment -> IntelligentCommentUiModel(project, comment).getCustomFoldRegionRenderer(project)
      else -> throw IllegalArgumentException(comment.javaClass.name)
    }
  }
}